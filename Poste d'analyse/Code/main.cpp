//------------------------------------------------------------------------------
/** @file         main.cpp
 *  @brief        Description rapide du fichier main.cpp
 *
 *  @author       Ianis Graton
 *  @author       STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
 *  @since        2012-01-03
 *  @version      0.1
 *  @date         2012-04-16
 *
 *  Le fichier main.cpp permet d'afficher l'interface graphique de la fen�tre principale
 *
 *  Fabrication   DevCPP, projet   .dev
 *
 *  @todo         Liste des choses restant � faire.
 *
 *  @bug          <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
 */
//------------------------------------------------------------------------------

#ifndef MAIN_CPP
#define MAIN_CPP

// En-t�tes standards necessaires dans ce fichier ------------------------------
#include <QtGui/QApplication>
using namespace std;

// En-t�te propre � l'application ----------------------------------------------
#include "mainwindow.h"

//------------------------------------------------------------------------------



int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    MainWindow w;
    w.show();

    return a.exec();
}

//---------------------------------------------------------------------------
#endif  // MAIN_CPP
//---------------------------------------------------------------------------
