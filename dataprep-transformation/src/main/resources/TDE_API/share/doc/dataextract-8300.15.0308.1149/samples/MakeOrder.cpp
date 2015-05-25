// -----------------------------------------------------------------------
// The information in this file is the property of Tableau Software and
// is confidential.
//
// Copyright (C) 2012  Tableau Software.
// Patents Pending.
// -----------------------------------------------------------------------
// externalapi/samples/MakeOrder.cpp
// -----------------------------------------------------------------------
#include "DataExtract_cpp.h"

#include <iomanip>
#include <iostream>
#include <memory>
#include <stdlib.h>

// Define the table's schema
void MakeTableDefinition( Tableau::TableDefinition& tableDef )
{
    using namespace Tableau;
    tableDef.SetDefaultCollation( Collation_en_GB );
    tableDef.AddColumn( L"Purchased",       Type_DateTime );
    tableDef.AddColumn( L"Product",         Type_CharString );
    tableDef.AddColumn( L"uProduct",        Type_UnicodeString );
    tableDef.AddColumn( L"Price",           Type_Double );
    tableDef.AddColumn( L"Quantity",        Type_Integer );
    tableDef.AddColumn( L"Taxed",           Type_Boolean );
    tableDef.AddColumn( L"Expiration Date", Type_Date );

    // Override default collation
    tableDef.AddColumnWithCollation( L"Produkt", Type_CharString, Collation_de );
}

// Print a Table's schema to stderr.
void PrintTableDefinition( Tableau::TableDefinition& tableDef )
{
    const int numColumns = tableDef.GetColumnCount();
    for ( int i = 0; i < numColumns; ++i ) {
        Tableau::Type type = tableDef.GetColumnType(i);
        std::wstring name = tableDef.GetColumnName(i);
        std::wcerr << L"Column " << i << L": " << name << L" ("
                   << std::hex << std::showbase << std::internal << std::setfill(L'0') << std::setw(6)
                   << type << std::dec << L")\n";
    }
}

// Insert a few rows of data.
void InsertData( std::shared_ptr<Tableau::Table> table )
{
    std::shared_ptr<Tableau::TableDefinition> tableDef = table->GetTableDefinition();
    Tableau::Row row( *tableDef );

    row.SetDateTime(  0, 2012, 7, 3, 11, 40, 12, 4550 ); // Purchased
    row.SetCharString(1, "Beans" );                      // Product
    row.SetString(    2, L"uniBeans" );                  // unicode Product
    row.SetDouble(    3, 1.08 );                         // Price
    row.SetDate(      6, 2029, 1, 1 );                   // Expiration date
    row.SetCharString(7, "Bohnen" );                     // Produkt
    for ( int i = 0; i < 10; ++i ) {
        row.SetInteger(4, i * 10)  ;                     // Quantity
        row.SetBoolean(5, i % 2 == 1 );                  // Taxed
        table->Insert( row );
    }
}

int main( int argc, char* argv[] )
{
    using namespace Tableau;

    try {
        Extract extract( L"order-cpp.tde" );
        std::shared_ptr<Table> table;

        if ( !extract.HasTable( L"Extract" ) ) {
            // Table does not exist; create it.
            TableDefinition tableDef;
            MakeTableDefinition( tableDef );
            table = extract.AddTable( L"Extract", tableDef );
        }
        else {
            // Open an existing table to add more rows.
            table = extract.OpenTable( L"Extract" );
        }

        std::shared_ptr<Tableau::TableDefinition> tableDef = table->GetTableDefinition();
        PrintTableDefinition( *tableDef );

        InsertData( table );
    }
    catch ( const TableauException& e) {
        std::wcerr << L"Something bad happened: " << e.GetMessage() << std::endl;
        exit( EXIT_FAILURE );
    }

    return 0;
}
