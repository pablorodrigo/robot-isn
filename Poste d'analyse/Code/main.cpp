//------------------------------------------------------------------------------
/** @file         main.cpp
 *  @brief        Description rapide du fichier main.cpp
 *
 *  @author       Ianis Graton
 *  @author       STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
 *  @since        2012-01-03
 *  @version      0.1
 *  @date         2012-04-16
 *
 *  Le fichier main.cpp permet d'afficher l'interface graphique de la fenêtre principale
 *
 *  Fabrication   DevCPP, projet   .dev
 *
 *  @todo         Liste des choses restant à faire.
 *
 *  @bug          <date du bug> - <CORRIGE> - <Intitulé précis du bug>
 */
//------------------------------------------------------------------------------

#ifndef MAIN_CPP
#define MAIN_CPP

// En-têtes standards necessaires dans ce fichier ------------------------------
#include <QtGui/QApplication>
using namespace std;

// En-tête propre à l'application ----------------------------------------------
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
