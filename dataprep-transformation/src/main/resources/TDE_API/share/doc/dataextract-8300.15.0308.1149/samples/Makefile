#
# Build and run C/C++ samples.
#
# Requires g++ 4.4 or later.

all : run-MakeOrder_c run-MakeOrder_cpp

LIBROOT = ../../../../lib64
CFLAGS  = -I$(LIBROOT)/../include
CXXFLAGS = $(CFLAGS) -std=c++0x
LIBS = -L$(LIBROOT)/dataextract -lDataExtract
LDFLAGS = -Wl,-rpath,$(LIBROOT)/dataextract

clean :
	rm -f MakeOrder_c MakeOrder_cpp order-c.tde order-cpp.tde DataExtract.log

MakeOrder_c : MakeOrder.c
	$(CC) $(CFLAGS) $(LDFLAGS) MakeOrder.c $(LIBS) -o MakeOrder_c

run-MakeOrder_c : MakeOrder_c
	rm -f order-c.tde
	./MakeOrder_c
	./MakeOrder_c

MakeOrder_cpp : MakeOrder.cpp
	$(CXX) $(CXXFLAGS) $(LDFLAGS) MakeOrder.cpp $(LIBS) -o MakeOrder_cpp

run-MakeOrder_cpp : MakeOrder_cpp
	rm -f order-cpp.tde
	./MakeOrder_cpp
	./MakeOrder_cpp
