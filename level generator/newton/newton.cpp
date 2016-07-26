#include<iostream>
#include<fstream>
#include<iomanip>
#include<string>
#include<sstream>
using namespace std;

double x[10];
double fx[10];
int pointsNum;

void fileLoad() {
     
     cout << "Wczytywanie pliku...\n";
     ifstream data;
     data.open("data.nwt");
     data >> pointsNum;
     for(int i = 0; i < pointsNum; i++) {
             data >> x[i];
             data >> fx[i];
     }
     return;
}

void keyboardLoad() {
     
     cout << "Liczba danych punktow: ";
     cin >> pointsNum;
     cout << endl;
    
     for(int i = 0; i < pointsNum; i++) {
             cout << "x" << i << " : ";
             cin >> x[i];
             cout << endl;
             cout << "f(x" << i << ") : ";
             cin >> fx[i];
             cout << "\n\n";
     }
     return;
}

void fileFormat() {
     
     string e;
     ifstream in;
     in.open("fx.txt");
     getline(in,e);
     in.close();
     
     char base, exp;
     int i; // iterator
     
     for(i = 0; i < e.length(); i++) {
           if(e.at(i) == '^') {
                      base = e.at(i-1);
                      exp = e.at(i+1);
                      
                      string s = "pow(";
                      ostringstream ss;
                      ss << base;
                      s += ss.str() + ",";
                      ostringstream ss2;
                      ss2 << exp;
                      s += ss2.str() + ")";
                      //string s = "pow("+base+","+exp+")";
                      
                      e.erase(i-1,3);
                      e.insert(i-1, s);
                      i+=7;
           }
     }
     
     ofstream out;
     out.open("fx.txt");
     out << e;
     out.close();
     
     return;
}

int main() {
    
    cout << "----------------------------------------------------\n";
    cout << "Program do aproksymacji wielomianowej metoda Newtona\n";
    cout << "----------------------------------------------------\n\n";
    
    cout << "Wybierz opcje wczytywania danych:\n\n"
         << "1. Dane z pliku .nwt\n"
         << "2. Dane wczytywane z klawiatury\n\n"
         << endl;
    
    short dataSource;
    cin >> dataSource;
    
    if(dataSource == 1) fileLoad();
    else if(dataSource == 2) keyboardLoad();
    
    double R[pointsNum][pointsNum];
    
    for(int i = 0; i < pointsNum; i++) {
            R[i][0] = fx[i];
    }
    
    // algorytm Newtona
    
    for(int i = 1; i < pointsNum; i++) {
            for(int j = 1; j <= i; j++) {
                    R[i][j] = (R[i][j-1] - R[i-1][j-1]) / (x[i] - x[i-j]);
            }
    }            
    
    // wypisywanie wynikow
    
    cout << endl;
    cout << "----------------------------------------------------\n";
    
    for(int i = 0; i < pointsNum; i++) {
            cout << setprecision(4) << fixed << R[i][i] << endl;
    }
    
    cout << "\n\n";
    
    // zapisywanie wynikow do pliku
    
    ofstream out;
    out.open("newton.txt");

    for(int i = 0; i < pointsNum; i++) {
            out << "(" << setprecision(8) << fixed << R[i][i] << ")";
            if(i > 0) out << "*";
            for(int j = 1; j <= i; j++) {
                    out << "(x-(" << x[j-1] << "))";
                    if(j <= i-1) out << "*";
            }
            if(i < pointsNum-1) out << " + ";           
    }
    
    cout << "Dane poprawnie zapisano do newton.txt";
    cout << "\n----------------------------------------------------\n";
    
    out.close();
    
    cout << "\nWykonywanie skryptu Mathematica...\n";
    
    // uruchamianie skryptu Mathematica
    
    system("newton.bat");
    
    cout << "\nDane zapisano do fx.txt";
    cout << "\nWykres obliczonego wielomianu zapisano do newton.png\n";
    
    // formatowanie pliku
    
    //cout << "Formatowanie fx.txt...\n";
    
    //fileFormat();
    
    //cout << "Plik poprawnie sformatowany\n\n";
    
    system("pause");
    return 0;   
}
