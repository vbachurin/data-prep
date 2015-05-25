// -----------------------------------------------------------------------
// Copyright (c) 2012 Tableau Software, Incorporated
//                    and its licensors. All rights reserved.
// Protected by U.S. Patent 7,089,266; Patents Pending.
//
// Portions of the code
// Copyright (c) 2002 The Board of Trustees of the Leland Stanford
//                    Junior University. All rights reserved.
// -----------------------------------------------------------------------
// DataExtract_cpp.h
// -----------------------------------------------------------------------
// WARNING: Computer generated file.  Do not hand modify.

#ifndef DataExtract_CPP_H
#define DataExtract_CPP_H

#include "DataExtract.h"
#include <cerrno>
#include <memory>
#include <string>

namespace Tableau {

typedef void* TableauHandle;

/*------------------------------------------------------------------------
  Type

  ------------------------------------------------------------------------*/

enum Type
{
    Type_Integer                          = 0x0007,    // TDE_DT_SINT64
    Type_Double                           = 0x000A,    // TDE_DT_DOUBLE
    Type_Boolean                          = 0x000B,    // TDE_DT_BOOL
    Type_Date                             = 0x000C,    // TDE_DT_DATE
    Type_DateTime                         = 0x000D,    // TDE_DT_DATETIME
    Type_Duration                         = 0x000E,    // TDE_DT_DURATION
    Type_CharString                       = 0x000F,    // TDE_DT_STR
    Type_UnicodeString                    = 0x0010,    // TDE_DT_WSTR
};

/*------------------------------------------------------------------------
  Result

  ------------------------------------------------------------------------*/

enum Result
{
    Result_Success                        = 0,         // Successful function call
    Result_OutOfMemory                    = ENOMEM,    // 
    Result_PermissionDenied               = EACCES,    // 
    Result_InvalidFile                    = EBADF,     // 
    Result_FileExists                     = EEXIST,    // 
    Result_TooManyFiles                   = EMFILE,    // 
    Result_FileNotFound                   = ENOENT,    // 
    Result_DiskFull                       = ENOSPC,    // 
    Result_DirectoryNotEmpty              = ENOTEMPTY, // 
    Result_NoSuchDatabase                 = 201,       // Data Engine errors start at 200.
    Result_QueryError                     = 202,       // 
    Result_NullArgument                   = 203,       // 
    Result_DataEngineError                = 204,       // 
    Result_Cancelled                      = 205,       // 
    Result_BadIndex                       = 206,       // 
    Result_ProtocolError                  = 207,       // 
    Result_NetworkError                   = 208,       // 
    Result_InternalError                  = 300,       // 300+: other error codes
    Result_WrongType                      = 301,       // 
    Result_UsageError                     = 302,       // 
    Result_InvalidArgument                = 303,       // 
    Result_BadHandle                      = 304,       // 
    Result_UnknownError                   = 999,       // 
};

/*------------------------------------------------------------------------
  Collation

  ------------------------------------------------------------------------*/

enum Collation
{
    Collation_Binary                      = 0,         // Internal binary representation
    Collation_ar                          = 1,         // Arabic
    Collation_cs                          = 2,         // Czech
    Collation_cs_CI                       = 3,         // Czech (Case Insensitive)
    Collation_cs_CI_AI                    = 4,         // Czech (Case/Accent Insensitive
    Collation_da                          = 5,         // Danish
    Collation_de                          = 6,         // German
    Collation_el                          = 7,         // Greek
    Collation_en_GB                       = 8,         // English (Great Britain)
    Collation_en_US                       = 9,         // English (US)
    Collation_en_US_CI                    = 10,        // English (US, Case Insensitive)
    Collation_es                          = 11,        // Spanish
    Collation_es_CI_AI                    = 12,        // Spanish (Case/Accent Insensitive)
    Collation_et                          = 13,        // Estonian
    Collation_fi                          = 14,        // Finnish
    Collation_fr_CA                       = 15,        // French (Canada)
    Collation_fr_FR                       = 16,        // French (France)
    Collation_fr_FR_CI_AI                 = 17,        // French (France, Case/Accent Insensitive)
    Collation_he                          = 18,        // Hebrew
    Collation_hu                          = 19,        // Hungarian
    Collation_is                          = 20,        // Icelandic
    Collation_it                          = 21,        // Italian
    Collation_ja                          = 22,        // Japanese
    Collation_ja_JIS                      = 23,        // Japanese (JIS)
    Collation_ko                          = 24,        // Korean
    Collation_lt                          = 25,        // Lithuanian
    Collation_lv                          = 26,        // Latvian
    Collation_nl_NL                       = 27,        // Dutch (Netherlands)
    Collation_nn                          = 28,        // Norwegian
    Collation_pl                          = 29,        // Polish
    Collation_pt_BR                       = 30,        // Portuguese (Brazil)
    Collation_pt_BR_CI_AI                 = 31,        // Portuguese (Brazil Case/Accent Insensitive)
    Collation_pt_PT                       = 32,        // Portuguese (Portugal)
    Collation_root                        = 33,        // Root
    Collation_ru                          = 34,        // Russian
    Collation_sl                          = 35,        // Slovenian
    Collation_sv_FI                       = 36,        // Swedish (Finland)
    Collation_sv_SE                       = 37,        // Swedish (Sweden)
    Collation_tr                          = 38,        // Turkish
    Collation_uk                          = 39,        // Ukrainian
    Collation_vi                          = 40,        // Vietnamese
    Collation_zh_Hans_CN                  = 41,        // Chinese (Simplified, China)
    Collation_zh_Hant_TW                  = 42,        // Chinese (Traditional, Taiwan)
};


} // namespace Tableau

#ifdef __GNUC__
#  if __GNUC__ < 4 || (__GNUC__ == 4 && __GNUC_MINOR__ < 6)
#    define nullptr NULL
#  endif
#endif

namespace Tableau {

/*------------------------------------------------------------------------
  CLASS
  TableDefinition

  Represents a collection of columns, or more specifically name/type pairs.

  ------------------------------------------------------------------------*/

class TableDefinition
{
  public:
    /// Creates an empty copy of a TableDefinition object, which represent a collection of columns.
    TableDefinition(
    );

    /// Closes the TableDefinition object and frees associated memory.
    void Close();

    /// Calls Close().
    ~TableDefinition();

    /// Gets the current default collation; if unspecified, default is binary.
    Collation
    GetDefaultCollation(
    );

    /// Sets the default collation for added string columns.
    void
    SetDefaultCollation(
        Collation collation
    );

    /// Adds a column to the table definition; the order in which columns are added implies their column number. String columns are defined with the current default collation.
    void
    AddColumn(
        std::wstring name,
        Type type
    );

    /// Adds a column with a specific collation.
    void
    AddColumnWithCollation(
        std::wstring name,
        Type type,
        Collation collation
    );

    /// Returns the number of columns in the table definition.
    int
    GetColumnCount(
    );

    /// Gives the name of the column.
    std::wstring
    GetColumnName(
        int columnNumber
    );

    /// Gives the type of the column.
    Type
    GetColumnType(
        int columnNumber
    );

    /// Gives the collation of the column.
    Collation
    GetColumnCollation(
        int columnNumber
    );


  private:
    TAB_HANDLE m_handle;

    // Forbidden:
    TableDefinition( const TableDefinition& );
    TableDefinition& operator=( const TableDefinition& );

    friend class Row;
    friend class Extract;
    friend class Table;
};

/*------------------------------------------------------------------------
  CLASS
  Row

  A tuple of values to be inserted into an extract.

  ------------------------------------------------------------------------*/

class Row
{
  public:
    /// Create an empty row with the specified schema.
    Row(
        TableDefinition& tableDefinition
    );

    /// Closes this Row and frees associated resources.
    void Close();

    /// Calls Close().
    ~Row();

    /// Sets a column in this row to null.
    void
    SetNull(
        int columnNumber
    );

    /// Sets a column in this row to the specified 32-bit unsigned integer value.
    void
    SetInteger(
        int columnNumber,
        int value
    );

    /// Sets a column in this row to the specified 64-bit unsigned integer value.
    void
    SetLongInteger(
        int columnNumber,
        int64_t value
    );

    /// Sets a column in this row to the specified double value.
    void
    SetDouble(
        int columnNumber,
        double value
    );

    /// Sets a column in this row to the specified boolean value.
    void
    SetBoolean(
        int columnNumber,
        bool value
    );

    /// Sets a column in this row to the specified string value.
    void
    SetString(
        int columnNumber,
        std::wstring value
    );

    /// Sets a column in this row to the specified string value.
    void
    SetCharString(
        int columnNumber,
        std::string value
    );

    /// Sets a column in this row to the specified date value.
    void
    SetDate(
        int columnNumber,
        int year,
        int month,
        int day
    );

    /// Sets a column in this row to the specified date/time value.
    void
    SetDateTime(
        int columnNumber,
        int year,
        int month,
        int day,
        int hour,
        int min,
        int sec,
        int frac
    );

    /// Sets a column in this row to the specified duration value.
    void
    SetDuration(
        int columnNumber,
        int day,
        int hour,
        int minute,
        int second,
        int frac
    );


  private:
    TAB_HANDLE m_handle;

    // Forbidden:
    Row( const Row& );
    Row& operator=( const Row& );

    friend class Table;
};

/*------------------------------------------------------------------------
  CLASS
  Table

  A table in the extract.

  ------------------------------------------------------------------------*/

class Table
{
  public:
    /// Queue a row for insertion; may perform insert of buffered rows.
    void
    Insert(
        Row& row
    );

    /// Get this table's schema.
    std::shared_ptr<TableDefinition>
    GetTableDefinition(
    );


  private:
    TAB_HANDLE m_handle;

    Table() : m_handle(nullptr) {}

    // Forbidden:
    Table( const Table& );
    Table& operator=( const Table& );

    friend class Extract;
};

/*------------------------------------------------------------------------
  CLASS
  Extract

  A Tableau Data Engine database.

  ------------------------------------------------------------------------*/

class Extract
{
  public:
    /// Create an extract object with an absolute or relative file system path. If the file already exists, following row insertion will be appended to the original file. This object must be closed.
    Extract(
        std::wstring path
    );

    /// Closes the extract and any open tables.
    void Close();

    /// Calls Close().
    ~Extract();

    /// Creates and adds table to the extract
    std::shared_ptr<Table>
    AddTable(
        std::wstring name,
        TableDefinition& tableDefinition
    );

    /// Opens an existing table in the extract.
    std::shared_ptr<Table>
    OpenTable(
        std::wstring name
    );

    /// Tests if a table exists in the extract.
    bool
    HasTable(
        std::wstring name
    );


  private:
    TAB_HANDLE m_handle;

    // Forbidden:
    Extract( const Extract& );
    Extract& operator=( const Extract& );

};

/*------------------------------------------------------------------------
  CLASS
  TableauException

  A general exception originating in Tableau code.

  ------------------------------------------------------------------------*/
class TableauException {
  public:
    TableauException( const TAB_RESULT r, const std::wstring m ) : m_result(r), m_message(m) {}

    const TAB_RESULT GetResultCode() const { return m_result; }
    const std::wstring GetMessage() const { return m_message; }

  private:
    const TAB_RESULT m_result;
    const std::wstring m_message;
};

namespace {

    std::basic_string<TableauWChar> MakeTableauString( const wchar_t* s )
    {
        const int len = static_cast<int>( wcslen(s) );
        TableauWChar* ts = new TableauWChar[len + 1];

        ToTableauString( s, ts );
        std::basic_string<TableauWChar> ret( ts );
        delete [] ts;

        return ret;
    }

    std::wstring ToStdString( TableauString s )
    {
        const int nChars = TableauStringLength( s ) + 1;

        wchar_t* ws = new wchar_t[nChars];

        FromTableauString( s, ws );
        std::wstring str( ws );
        delete [] ws;

        return str;
    }
}



// -----------------------------------------------------------------------
// TableDefinition methods
// -----------------------------------------------------------------------

// Creates an empty copy of a TableDefinition object, which represent a collection of columns.
TableDefinition::TableDefinition(
)
{
    TAB_RESULT result = TabTableDefinitionCreate(
        &m_handle
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Closes the TableDefinition object and frees associated memory.
TableDefinition::~TableDefinition()
{
    Close();
}

void TableDefinition::Close()
{
    if ( m_handle != nullptr ) {
        int result = TabTableDefinitionClose( m_handle );
        m_handle = nullptr;

        if ( result != TAB_RESULT_Success )
            throw TableauException( result, TabGetLastErrorMessage() );
    }
}

// Gets the current default collation; if unspecified, default is binary.
Collation
TableDefinition::GetDefaultCollation(
)
{
    TAB_COLLATION retval;
    TAB_RESULT result = TabTableDefinitionGetDefaultCollation(m_handle
        , &retval
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );

    return static_cast< Collation >( retval );
}

// Sets the default collation for added string columns.
void
TableDefinition::SetDefaultCollation(
    Collation collation
)
{
    TAB_RESULT result = TabTableDefinitionSetDefaultCollation(m_handle
        , collation
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Adds a column to the table definition; the order in which columns are added implies their column number. String columns are defined with the current default collation.
void
TableDefinition::AddColumn(
    std::wstring name,
    Type type
)
{
    TAB_RESULT result = TabTableDefinitionAddColumn(m_handle
        , MakeTableauString(name.c_str()).c_str()
        , type
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Adds a column with a specific collation.
void
TableDefinition::AddColumnWithCollation(
    std::wstring name,
    Type type,
    Collation collation
)
{
    TAB_RESULT result = TabTableDefinitionAddColumnWithCollation(m_handle
        , MakeTableauString(name.c_str()).c_str()
        , type
        , collation
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Returns the number of columns in the table definition.
int
TableDefinition::GetColumnCount(
)
{
    int retval;
    TAB_RESULT result = TabTableDefinitionGetColumnCount(m_handle
        , &retval
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );

    return static_cast< int >( retval );
}

// Gives the name of the column.
std::wstring
TableDefinition::GetColumnName(
    int columnNumber
)
{
    TableauString retval;
    TAB_RESULT result = TabTableDefinitionGetColumnName(m_handle
        , columnNumber
        , &retval
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );

    return ToStdString( retval );
}

// Gives the type of the column.
Type
TableDefinition::GetColumnType(
    int columnNumber
)
{
    TAB_TYPE retval;
    TAB_RESULT result = TabTableDefinitionGetColumnType(m_handle
        , columnNumber
        , &retval
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );

    return static_cast< Type >( retval );
}

// Gives the collation of the column.
Collation
TableDefinition::GetColumnCollation(
    int columnNumber
)
{
    TAB_COLLATION retval;
    TAB_RESULT result = TabTableDefinitionGetColumnCollation(m_handle
        , columnNumber
        , &retval
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );

    return static_cast< Collation >( retval );
}


// -----------------------------------------------------------------------
// Row methods
// -----------------------------------------------------------------------

// Create an empty row with the specified schema.
Row::Row(
    TableDefinition& tableDefinition
)
{
    TAB_RESULT result = TabRowCreate(
        &m_handle
        , tableDefinition.m_handle
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Closes this Row and frees associated resources.
Row::~Row()
{
    Close();
}

void Row::Close()
{
    if ( m_handle != nullptr ) {
        int result = TabRowClose( m_handle );
        m_handle = nullptr;

        if ( result != TAB_RESULT_Success )
            throw TableauException( result, TabGetLastErrorMessage() );
    }
}

// Sets a column in this row to null.
void
Row::SetNull(
    int columnNumber
)
{
    TAB_RESULT result = TabRowSetNull(m_handle
        , columnNumber
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Sets a column in this row to the specified 32-bit unsigned integer value.
void
Row::SetInteger(
    int columnNumber,
    int value
)
{
    TAB_RESULT result = TabRowSetInteger(m_handle
        , columnNumber
        , value
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Sets a column in this row to the specified 64-bit unsigned integer value.
void
Row::SetLongInteger(
    int columnNumber,
    int64_t value
)
{
    TAB_RESULT result = TabRowSetLongInteger(m_handle
        , columnNumber
        , value
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Sets a column in this row to the specified double value.
void
Row::SetDouble(
    int columnNumber,
    double value
)
{
    TAB_RESULT result = TabRowSetDouble(m_handle
        , columnNumber
        , value
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Sets a column in this row to the specified boolean value.
void
Row::SetBoolean(
    int columnNumber,
    bool value
)
{
    TAB_RESULT result = TabRowSetBoolean(m_handle
        , columnNumber
        , value
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Sets a column in this row to the specified string value.
void
Row::SetString(
    int columnNumber,
    std::wstring value
)
{
    TAB_RESULT result = TabRowSetString(m_handle
        , columnNumber
        , MakeTableauString(value.c_str()).c_str()
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Sets a column in this row to the specified string value.
void
Row::SetCharString(
    int columnNumber,
    std::string value
)
{
    TAB_RESULT result = TabRowSetCharString(m_handle
        , columnNumber
        , value.c_str()
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Sets a column in this row to the specified date value.
void
Row::SetDate(
    int columnNumber,
    int year,
    int month,
    int day
)
{
    TAB_RESULT result = TabRowSetDate(m_handle
        , columnNumber
        , year
        , month
        , day
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Sets a column in this row to the specified date/time value.
void
Row::SetDateTime(
    int columnNumber,
    int year,
    int month,
    int day,
    int hour,
    int min,
    int sec,
    int frac
)
{
    TAB_RESULT result = TabRowSetDateTime(m_handle
        , columnNumber
        , year
        , month
        , day
        , hour
        , min
        , sec
        , frac
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Sets a column in this row to the specified duration value.
void
Row::SetDuration(
    int columnNumber,
    int day,
    int hour,
    int minute,
    int second,
    int frac
)
{
    TAB_RESULT result = TabRowSetDuration(m_handle
        , columnNumber
        , day
        , hour
        , minute
        , second
        , frac
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}


// -----------------------------------------------------------------------
// Table methods
// -----------------------------------------------------------------------

// Queue a row for insertion; may perform insert of buffered rows.
void
Table::Insert(
    Row& row
)
{
    TAB_RESULT result = TabTableInsert(m_handle
        , row.m_handle
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Get this table's schema.
std::shared_ptr<TableDefinition>
Table::GetTableDefinition(
)
{
    TAB_HANDLE retval;
    TAB_RESULT result = TabTableGetTableDefinition(m_handle
        , &retval
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );

    std::shared_ptr<TableDefinition> ret = std::shared_ptr<TableDefinition>(new TableDefinition);
    ret->m_handle = retval;
    return ret;
}


// -----------------------------------------------------------------------
// Extract methods
// -----------------------------------------------------------------------

// Create an extract object with an absolute or relative file system path. If the file already exists, following row insertion will be appended to the original file. This object must be closed.
Extract::Extract(
    std::wstring path
)
{
    TAB_RESULT result = TabExtractCreate(
        &m_handle
        , MakeTableauString(path.c_str()).c_str()
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );
}

// Closes the extract and any open tables.
Extract::~Extract()
{
    Close();
}

void Extract::Close()
{
    if ( m_handle != nullptr ) {
        int result = TabExtractClose( m_handle );
        m_handle = nullptr;

        if ( result != TAB_RESULT_Success )
            throw TableauException( result, TabGetLastErrorMessage() );
    }
}

// Creates and adds table to the extract
std::shared_ptr<Table>
Extract::AddTable(
    std::wstring name,
    TableDefinition& tableDefinition
)
{
    TAB_HANDLE retval;
    TAB_RESULT result = TabExtractAddTable(m_handle
        , MakeTableauString(name.c_str()).c_str()
        , tableDefinition.m_handle
        , &retval
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );

    std::shared_ptr<Table> ret = std::shared_ptr<Table>(new Table);
    ret->m_handle = retval;
    return ret;
}

// Opens an existing table in the extract.
std::shared_ptr<Table>
Extract::OpenTable(
    std::wstring name
)
{
    TAB_HANDLE retval;
    TAB_RESULT result = TabExtractOpenTable(m_handle
        , MakeTableauString(name.c_str()).c_str()
        , &retval
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );

    std::shared_ptr<Table> ret = std::shared_ptr<Table>(new Table);
    ret->m_handle = retval;
    return ret;
}

// Tests if a table exists in the extract.
bool
Extract::HasTable(
    std::wstring name
)
{
    int retval;
    TAB_RESULT result = TabExtractHasTable(m_handle
        , MakeTableauString(name.c_str()).c_str()
        , &retval
    );

    if ( result != TAB_RESULT_Success )
        throw TableauException( result, TabGetLastErrorMessage() );

    return retval != 0;
}

} // namespace Tableau
#endif // DataExtract_CPP_H
