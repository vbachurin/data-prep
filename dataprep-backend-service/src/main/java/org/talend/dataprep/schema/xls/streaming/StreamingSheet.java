// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.schema.xls.streaming;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.PaneInformation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

public class StreamingSheet implements Sheet {

    private final String name;

    private final StreamingSheetReader reader;

    public StreamingSheet(String name, StreamingSheetReader reader) {
        this.name = name;
        this.reader = reader;
    }

    public StreamingSheetReader getReader() {
        return reader;
    }

    /* Supported */

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Row> iterator() {
        return reader.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Row> rowIterator() {
        return reader.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSheetName() {
        return name;
    }

    /* Unsupported */

    /**
     * Not supported
     */
    @Override
    public Row createRow(int rownum) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void removeRow(Row row) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public Row getRow(int rownum) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int getPhysicalNumberOfRows() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int getFirstRowNum() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int getLastRowNum() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setColumnHidden(int columnIndex, boolean hidden) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isColumnHidden(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setRightToLeft(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isRightToLeft() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setColumnWidth(int columnIndex, int width) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int getColumnWidth(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public float getColumnWidthInPixels(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setDefaultColumnWidth(int width) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int getDefaultColumnWidth() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public short getDefaultRowHeight() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public float getDefaultRowHeightInPoints() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setDefaultRowHeight(short height) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setDefaultRowHeightInPoints(float height) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public CellStyle getColumnStyle(int column) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int addMergedRegion(CellRangeAddress region) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setVerticallyCenter(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setHorizontallyCenter(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getHorizontallyCenter() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getVerticallyCenter() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void removeMergedRegion(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int getNumMergedRegions() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public CellRangeAddress getMergedRegion(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public List<CellRangeAddress> getMergedRegions() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setForceFormulaRecalculation(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getForceFormulaRecalculation() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setAutobreaks(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setDisplayGuts(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setDisplayZeros(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isDisplayZeros() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setFitToPage(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setRowSumsBelow(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setRowSumsRight(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getAutobreaks() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getDisplayGuts() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getFitToPage() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getRowSumsBelow() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getRowSumsRight() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isPrintGridlines() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setPrintGridlines(boolean show) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public PrintSetup getPrintSetup() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public Header getHeader() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public Footer getFooter() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setSelected(boolean value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public double getMargin(short margin) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setMargin(short margin, double size) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getProtect() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void protectSheet(String password) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean getScenarioProtect() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setZoom(int numerator, int denominator) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public short getTopRow() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public short getLeftCol() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void showInPane(int toprow, int leftcol) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void shiftRows(int startRow, int endRow, int n) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void shiftRows(int startRow, int endRow, int n, boolean copyRowHeight, boolean resetOriginalRowHeight) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void createFreezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void createFreezePane(int colSplit, int rowSplit) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void createSplitPane(int xSplitPos, int ySplitPos, int leftmostColumn, int topRow, int activePane) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public PaneInformation getPaneInformation() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setDisplayGridlines(boolean show) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isDisplayGridlines() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setDisplayFormulas(boolean show) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isDisplayFormulas() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setDisplayRowColHeadings(boolean show) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isDisplayRowColHeadings() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setRowBreak(int row) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isRowBroken(int row) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void removeRowBreak(int row) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int[] getRowBreaks() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int[] getColumnBreaks() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setColumnBreak(int column) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isColumnBroken(int column) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void removeColumnBreak(int column) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setColumnGroupCollapsed(int columnNumber, boolean collapsed) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void groupColumn(int fromColumn, int toColumn) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void ungroupColumn(int fromColumn, int toColumn) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void groupRow(int fromRow, int toRow) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void ungroupRow(int fromRow, int toRow) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setRowGroupCollapsed(int row, boolean collapse) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setDefaultColumnStyle(int column, CellStyle style) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void autoSizeColumn(int column) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void autoSizeColumn(int column, boolean useMergedCells) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public Comment getCellComment(int row, int column) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public Drawing createDrawingPatriarch() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public Workbook getWorkbook() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public boolean isSelected() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public CellRange<? extends Cell> setArrayFormula(String formula, CellRangeAddress range) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public CellRange<? extends Cell> removeArrayFormula(Cell cell) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public DataValidationHelper getDataValidationHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public List<? extends DataValidation> getDataValidations() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void addValidationData(DataValidation dataValidation) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public AutoFilter setAutoFilter(CellRangeAddress range) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public SheetConditionalFormatting getSheetConditionalFormatting() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public CellRangeAddress getRepeatingRows() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public CellRangeAddress getRepeatingColumns() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setRepeatingRows(CellRangeAddress rowRangeRef) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public void setRepeatingColumns(CellRangeAddress columnRangeRef) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     */
    @Override
    public int getColumnOutlineLevel(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CellAddress getActiveCell() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comment getCellComment(CellAddress ref) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<CellAddress, ? extends Comment> getCellComments() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Drawing getDrawingPatriarch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Hyperlink getHyperlink(int row, int column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends Hyperlink> getHyperlinkList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setActiveCell(CellAddress address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setZoom(int scale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int addMergedRegionUnsafe(CellRangeAddress region) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void validateMergedRegions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeMergedRegions(Collection<Integer> indices) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPrintRowAndColumnHeadings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPrintRowAndColumnHeadings(boolean show) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Hyperlink getHyperlink(CellAddress addr) {
        throw new UnsupportedOperationException();
    }
}
