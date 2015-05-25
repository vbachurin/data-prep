// -----------------------------------------------------------------------
// Copyright (c) 2012 Tableau Software, Incorporated
//                    and its licensors. All rights reserved.
// Protected by U.S. Patent 7,089,266; Patents Pending.
//
// Portions of the code
// Copyright (c) 2002 The Board of Trustees of the Leland Stanford
//                    Junior University. All rights reserved.
// -----------------------------------------------------------------------
// DataExtract.h
// -----------------------------------------------------------------------
// WARNING: Computer generated file.  Do not hand modify.

#ifndef DataExtract_H
#define DataExtract_H

#include <stdint.h>
#include <wchar.h>

#if defined(_WIN32)
#  ifdef TBL_EXTERNALTDE_EXPORTS
#    define TAB_API __declspec(dllexport)
#  else
#    define TAB_API __declspec(dllimport)
#  endif
#elif defined(__APPLE__)
#  ifdef TBL_EXTERNALTDE_EXPORTS
#    define TAB_API __attribute__ ((visibility ("default")))
#  else
#    define TAB_API
#  endif
#else
#  define TAB_API
#endif

typedef void*                 TAB_HANDLE;
typedef unsigned short        TableauWChar;
typedef const TableauWChar*   TableauString;
typedef const char*           TableauCharString;

#ifdef __cplusplus
extern "C" {
#endif

/*------------------------------------------------------------------------
  TAB_TYPE

  ------------------------------------------------------------------------*/

typedef int32_t TAB_TYPE;
TAB_API const extern TAB_TYPE TAB_TYPE_Integer;
TAB_API const extern TAB_TYPE TAB_TYPE_Double;
TAB_API const extern TAB_TYPE TAB_TYPE_Boolean;
TAB_API const extern TAB_TYPE TAB_TYPE_Date;
TAB_API const extern TAB_TYPE TAB_TYPE_DateTime;
TAB_API const extern TAB_TYPE TAB_TYPE_Duration;
TAB_API const extern TAB_TYPE TAB_TYPE_CharString;
TAB_API const extern TAB_TYPE TAB_TYPE_UnicodeString;

/*------------------------------------------------------------------------
  TAB_RESULT

  ------------------------------------------------------------------------*/

typedef int32_t TAB_RESULT;
TAB_API const extern TAB_RESULT TAB_RESULT_Success;
TAB_API const extern TAB_RESULT TAB_RESULT_OutOfMemory;
TAB_API const extern TAB_RESULT TAB_RESULT_PermissionDenied;
TAB_API const extern TAB_RESULT TAB_RESULT_InvalidFile;
TAB_API const extern TAB_RESULT TAB_RESULT_FileExists;
TAB_API const extern TAB_RESULT TAB_RESULT_TooManyFiles;
TAB_API const extern TAB_RESULT TAB_RESULT_FileNotFound;
TAB_API const extern TAB_RESULT TAB_RESULT_DiskFull;
TAB_API const extern TAB_RESULT TAB_RESULT_DirectoryNotEmpty;
TAB_API const extern TAB_RESULT TAB_RESULT_NoSuchDatabase;
TAB_API const extern TAB_RESULT TAB_RESULT_QueryError;
TAB_API const extern TAB_RESULT TAB_RESULT_NullArgument;
TAB_API const extern TAB_RESULT TAB_RESULT_DataEngineError;
TAB_API const extern TAB_RESULT TAB_RESULT_Cancelled;
TAB_API const extern TAB_RESULT TAB_RESULT_BadIndex;
TAB_API const extern TAB_RESULT TAB_RESULT_ProtocolError;
TAB_API const extern TAB_RESULT TAB_RESULT_NetworkError;
TAB_API const extern TAB_RESULT TAB_RESULT_InternalError;
TAB_API const extern TAB_RESULT TAB_RESULT_WrongType;
TAB_API const extern TAB_RESULT TAB_RESULT_UsageError;
TAB_API const extern TAB_RESULT TAB_RESULT_InvalidArgument;
TAB_API const extern TAB_RESULT TAB_RESULT_BadHandle;
TAB_API const extern TAB_RESULT TAB_RESULT_UnknownError;

/*------------------------------------------------------------------------
  TAB_COLLATION

  ------------------------------------------------------------------------*/

typedef int32_t TAB_COLLATION;
TAB_API const extern TAB_COLLATION TAB_COLLATION_Binary;
TAB_API const extern TAB_COLLATION TAB_COLLATION_ar;
TAB_API const extern TAB_COLLATION TAB_COLLATION_cs;
TAB_API const extern TAB_COLLATION TAB_COLLATION_cs_CI;
TAB_API const extern TAB_COLLATION TAB_COLLATION_cs_CI_AI;
TAB_API const extern TAB_COLLATION TAB_COLLATION_da;
TAB_API const extern TAB_COLLATION TAB_COLLATION_de;
TAB_API const extern TAB_COLLATION TAB_COLLATION_el;
TAB_API const extern TAB_COLLATION TAB_COLLATION_en_GB;
TAB_API const extern TAB_COLLATION TAB_COLLATION_en_US;
TAB_API const extern TAB_COLLATION TAB_COLLATION_en_US_CI;
TAB_API const extern TAB_COLLATION TAB_COLLATION_es;
TAB_API const extern TAB_COLLATION TAB_COLLATION_es_CI_AI;
TAB_API const extern TAB_COLLATION TAB_COLLATION_et;
TAB_API const extern TAB_COLLATION TAB_COLLATION_fi;
TAB_API const extern TAB_COLLATION TAB_COLLATION_fr_CA;
TAB_API const extern TAB_COLLATION TAB_COLLATION_fr_FR;
TAB_API const extern TAB_COLLATION TAB_COLLATION_fr_FR_CI_AI;
TAB_API const extern TAB_COLLATION TAB_COLLATION_he;
TAB_API const extern TAB_COLLATION TAB_COLLATION_hu;
TAB_API const extern TAB_COLLATION TAB_COLLATION_is;
TAB_API const extern TAB_COLLATION TAB_COLLATION_it;
TAB_API const extern TAB_COLLATION TAB_COLLATION_ja;
TAB_API const extern TAB_COLLATION TAB_COLLATION_ja_JIS;
TAB_API const extern TAB_COLLATION TAB_COLLATION_ko;
TAB_API const extern TAB_COLLATION TAB_COLLATION_lt;
TAB_API const extern TAB_COLLATION TAB_COLLATION_lv;
TAB_API const extern TAB_COLLATION TAB_COLLATION_nl_NL;
TAB_API const extern TAB_COLLATION TAB_COLLATION_nn;
TAB_API const extern TAB_COLLATION TAB_COLLATION_pl;
TAB_API const extern TAB_COLLATION TAB_COLLATION_pt_BR;
TAB_API const extern TAB_COLLATION TAB_COLLATION_pt_BR_CI_AI;
TAB_API const extern TAB_COLLATION TAB_COLLATION_pt_PT;
TAB_API const extern TAB_COLLATION TAB_COLLATION_root;
TAB_API const extern TAB_COLLATION TAB_COLLATION_ru;
TAB_API const extern TAB_COLLATION TAB_COLLATION_sl;
TAB_API const extern TAB_COLLATION TAB_COLLATION_sv_FI;
TAB_API const extern TAB_COLLATION TAB_COLLATION_sv_SE;
TAB_API const extern TAB_COLLATION TAB_COLLATION_tr;
TAB_API const extern TAB_COLLATION TAB_COLLATION_uk;
TAB_API const extern TAB_COLLATION TAB_COLLATION_vi;
TAB_API const extern TAB_COLLATION TAB_COLLATION_zh_Hans_CN;
TAB_API const extern TAB_COLLATION TAB_COLLATION_zh_Hant_TW;


#ifdef __cplusplus
}
#endif


#ifdef __cplusplus
extern "C" {
#endif

TAB_API const wchar_t *TabGetLastErrorMessage();
TAB_API void TabShutdown();

/// Convert a wide string to a TableauString.
/// @param ws The null-terminated string to convert.
/// @param ts Buffer for null-terminated output; presumed to be large enough.
TAB_API void ToTableauString( const wchar_t* ws, TableauWChar* ts );

/// Convert a TableauString to a wide string.
/// @param ts The null-terminated TableauString to convert.
/// @param ws Buffer for null-terminated output; presumed to be large enough.
TAB_API void FromTableauString( const TableauString ts, wchar_t* ws );

/// Measure the length of a null-terminated Tableau String.
TAB_API int TableauStringLength( const TableauString ts );

/*------------------------------------------------------------------------
  SECTION
  TableDefinition

  Represents a collection of columns, or more specifically name/type pairs.

  ------------------------------------------------------------------------*/

/// Creates an empty copy of a TableDefinition object, which represent a collection of columns.
TAB_API TAB_RESULT TabTableDefinitionCreate(
    TAB_HANDLE *handle
);

/// Closes the TableDefinition object and frees associated memory.
TAB_API TAB_RESULT TabTableDefinitionClose(TAB_HANDLE handle);

/// Gets the current default collation; if unspecified, default is binary.
TAB_API TAB_RESULT TabTableDefinitionGetDefaultCollation(
      TAB_HANDLE TableDefinition
    , TAB_COLLATION* retval
    );

/// Sets the default collation for added string columns.
TAB_API TAB_RESULT TabTableDefinitionSetDefaultCollation(
      TAB_HANDLE TableDefinition
    , TAB_COLLATION collation
    );

/// Adds a column to the table definition; the order in which columns are added implies their column number. String columns are defined with the current default collation.
TAB_API TAB_RESULT TabTableDefinitionAddColumn(
      TAB_HANDLE TableDefinition
    , TableauString name
    , TAB_TYPE type
    );

/// Adds a column with a specific collation.
TAB_API TAB_RESULT TabTableDefinitionAddColumnWithCollation(
      TAB_HANDLE TableDefinition
    , TableauString name
    , TAB_TYPE type
    , TAB_COLLATION collation
    );

/// Returns the number of columns in the table definition.
TAB_API TAB_RESULT TabTableDefinitionGetColumnCount(
      TAB_HANDLE TableDefinition
    , int* retval
    );

/// Gives the name of the column.
TAB_API TAB_RESULT TabTableDefinitionGetColumnName(
      TAB_HANDLE TableDefinition
    , int columnNumber
    , TableauString* retval
    );

/// Gives the type of the column.
TAB_API TAB_RESULT TabTableDefinitionGetColumnType(
      TAB_HANDLE TableDefinition
    , int columnNumber
    , TAB_TYPE* retval
    );

/// Gives the collation of the column.
TAB_API TAB_RESULT TabTableDefinitionGetColumnCollation(
      TAB_HANDLE TableDefinition
    , int columnNumber
    , TAB_COLLATION* retval
    );


/*------------------------------------------------------------------------
  SECTION
  Row

  A tuple of values to be inserted into an extract.

  ------------------------------------------------------------------------*/

/// Create an empty row with the specified schema.
TAB_API TAB_RESULT TabRowCreate(
    TAB_HANDLE *handle
    , TAB_HANDLE tableDefinition
);

/// Closes this Row and frees associated resources.
TAB_API TAB_RESULT TabRowClose(TAB_HANDLE handle);

/// Sets a column in this row to null.
TAB_API TAB_RESULT TabRowSetNull(
      TAB_HANDLE Row
    , int columnNumber
    );

/// Sets a column in this row to the specified 32-bit unsigned integer value.
TAB_API TAB_RESULT TabRowSetInteger(
      TAB_HANDLE Row
    , int columnNumber
    , int value
    );

/// Sets a column in this row to the specified 64-bit unsigned integer value.
TAB_API TAB_RESULT TabRowSetLongInteger(
      TAB_HANDLE Row
    , int columnNumber
    , int64_t value
    );

/// Sets a column in this row to the specified double value.
TAB_API TAB_RESULT TabRowSetDouble(
      TAB_HANDLE Row
    , int columnNumber
    , double value
    );

/// Sets a column in this row to the specified boolean value.
TAB_API TAB_RESULT TabRowSetBoolean(
      TAB_HANDLE Row
    , int columnNumber
    , int value
    );

/// Sets a column in this row to the specified string value.
TAB_API TAB_RESULT TabRowSetString(
      TAB_HANDLE Row
    , int columnNumber
    , TableauString value
    );

/// Sets a column in this row to the specified string value.
TAB_API TAB_RESULT TabRowSetCharString(
      TAB_HANDLE Row
    , int columnNumber
    , TableauCharString value
    );

/// Sets a column in this row to the specified date value.
TAB_API TAB_RESULT TabRowSetDate(
      TAB_HANDLE Row
    , int columnNumber
    , int year
    , int month
    , int day
    );

/// Sets a column in this row to the specified date/time value.
TAB_API TAB_RESULT TabRowSetDateTime(
      TAB_HANDLE Row
    , int columnNumber
    , int year
    , int month
    , int day
    , int hour
    , int min
    , int sec
    , int frac
    );

/// Sets a column in this row to the specified duration value.
TAB_API TAB_RESULT TabRowSetDuration(
      TAB_HANDLE Row
    , int columnNumber
    , int day
    , int hour
    , int minute
    , int second
    , int frac
    );


/*------------------------------------------------------------------------
  SECTION
  Table

  A table in the extract.

  ------------------------------------------------------------------------*/

/// Queue a row for insertion; may perform insert of buffered rows.
TAB_API TAB_RESULT TabTableInsert(
      TAB_HANDLE Table
    , TAB_HANDLE row
    );

/// Get this table's schema.
TAB_API TAB_RESULT TabTableGetTableDefinition(
      TAB_HANDLE Table
    , TAB_HANDLE* retval
    );


/*------------------------------------------------------------------------
  SECTION
  Extract

  A Tableau Data Engine database.

  ------------------------------------------------------------------------*/

/// Create an extract object with an absolute or relative file system path. If the file already exists, following row insertion will be appended to the original file. This object must be closed.
TAB_API TAB_RESULT TabExtractCreate(
    TAB_HANDLE *handle
    , TableauString path
);

/// Closes the extract and any open tables.
TAB_API TAB_RESULT TabExtractClose(TAB_HANDLE handle);

/// Creates and adds table to the extract
TAB_API TAB_RESULT TabExtractAddTable(
      TAB_HANDLE Extract
    , TableauString name
    , TAB_HANDLE tableDefinition
    , TAB_HANDLE* retval
    );

/// Opens an existing table in the extract.
TAB_API TAB_RESULT TabExtractOpenTable(
      TAB_HANDLE Extract
    , TableauString name
    , TAB_HANDLE* retval
    );

/// Tests if a table exists in the extract.
TAB_API TAB_RESULT TabExtractHasTable(
      TAB_HANDLE Extract
    , TableauString name
    , int* retval
    );



#ifdef __cplusplus
}
#endif


#endif // DataExtract_H
