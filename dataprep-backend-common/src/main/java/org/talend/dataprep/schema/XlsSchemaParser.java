package org.talend.dataprep.schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.type.Type;

/**
 * This class is responsible to parse excel file (note poi is used for reading .xls)
 */
@Service("schemaParser#xls")
public class XlsSchemaParser implements SchemaParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<ColumnMetadata> parse(InputStream content) {
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(content);

            // ATM only first sheet but need to be discuss
            // maybe return List<List<ColumnMetadata>> ??
            // so we couuld parse all sheets
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);

            if (sheet == null) {
                return Collections.emptyList();
            }

            return parsePerSheet(sheet);

        } catch (IOException e) {
            logger.debug("IOEXception during parsing xls content :" + e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected List<ColumnMetadata> parsePerSheet(HSSFSheet sheet) {

        SortedMap<Integer, SortedMap<Integer, Type>> cellsTypeMatrix = collectSheetTypeMatrix(sheet);

        logger.debug("cellsTypeMatrix: {}", cellsTypeMatrix);

        Map<Integer, Integer> cellTypeChange = guessHeaderChange(cellsTypeMatrix);


        // average cell type change
        int sum = 0;
        for (Map.Entry<Integer, Integer> entry : cellTypeChange.entrySet()) {
            sum += entry.getValue();
        }

        // so header size is calculated on the average of value type change for column
        // I agree it's not the best :-)

        final int averageHeaderSize = Math.floorDiv(sum, cellTypeChange.size());

        logger.debug("averageHeaderSize: {}, cellTypeChange: {}", averageHeaderSize, cellTypeChange);

        // here we have informations regarding types for each rows/col (yup a Matrix!! :-) )
        // so we can analyse and guess metadatas
        final List<ColumnMetadata> columnMetadatas = new ArrayList<>(cellsTypeMatrix.size());

        cellsTypeMatrix.forEach((integer, integerTypeSortedMap) -> {

            // we guess the type is the one with the type change
                int colRowTypeChange = cellTypeChange.get(integer);

                Type type = integerTypeSortedMap.get(colRowTypeChange);

                // TODO quality

                columnMetadatas.add(ColumnMetadata.Builder //
                        .column() //
                        .headerSize(averageHeaderSize) //
                        .name("col" + integer) //
                        .type(type) //
                        .build());

            });

        return columnMetadatas;
    }

    /**
     * we store cell types per with the row list
     * 
     * @param sheet key is the column number, value is a Map with key row number and value Type
     * @return
     */
    protected SortedMap<Integer, SortedMap<Integer, Type>> collectSheetTypeMatrix(HSSFSheet sheet) {
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        logger.debug("firstRowNum: {}, lastRowNum: {}", firstRowNum, lastRowNum);

        SortedMap<Integer, SortedMap<Integer, Type>> cellsTypeMatrix = new TreeMap<>();

        // we start analysing rows
        for (int rowCounter = firstRowNum; rowCounter <= lastRowNum; rowCounter++) {

            int cellCounter = 0;
            Iterator<Cell> cellIterator = sheet.getRow(rowCounter).cellIterator();

            Type currentType;

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    currentType = Type.BOOLEAN;
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    currentType = Type.NUMERIC;
                    break;
                case Cell.CELL_TYPE_BLANK:
                case Cell.CELL_TYPE_STRING:
                    currentType = Type.STRING;
                    break;
                case Cell.CELL_TYPE_ERROR | Cell.CELL_TYPE_FORMULA:
                    // we cannot really do anything with a formula
                default:
                    currentType = Type.ANY;

                }

                SortedMap<Integer, Type> cellInfo = cellsTypeMatrix.get(cellCounter);

                if (cellInfo == null) {
                    cellInfo = new TreeMap<>();
                }
                cellInfo.put(rowCounter, currentType);

                cellsTypeMatrix.put(cellCounter, cellInfo);
                cellCounter++;
            }

        }

        return cellsTypeMatrix;
    }

    /**
     * <p>
     * As we can try to be smart and user friendly and not those nerd devs who doesn't mind about users so we try to
     * guess the header size (we assume those bloody users doesn't have complicated headers!!)
     * </p>
     * <p>
     * we scan all entries to find a common header size value (i.e row line with value type change) more simple all
     * columns/lines with type String
     * </p>
     * 
     * @param cellsTypeMatrix key: column number value: row where the type change from String to something else
     * @return
     */
    protected SortedMap<Integer, Integer> guessHeaderChange(Map<Integer, SortedMap<Integer, Type>> cellsTypeMatrix) {
        SortedMap<Integer, Integer> cellTypeChange = new TreeMap<>();

        cellsTypeMatrix.forEach((integer, integerTypeSortedMap) -> {

            Type firstType = null;
            int rowChange = 0;

            for (Map.Entry<Integer, Type> sortedMapEntry : integerTypeSortedMap.entrySet()) {
                if (firstType == null) {
                    firstType = sortedMapEntry.getValue();
                } else {
                    if (sortedMapEntry.getValue() != firstType && sortedMapEntry.getValue() != Type.STRING) {
                        rowChange = sortedMapEntry.getKey();
                        break;
                    }
                }
            }

            cellTypeChange.put(integer, rowChange);

            firstType = null;
            rowChange = 0;

        });

        return cellTypeChange;
    }
}
