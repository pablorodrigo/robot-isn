//------------------------------------------------------------------------------
/** @file         EchangeBinaire.cpp
 *  @brief        Le fichier EchangeBinaire.cpp permet de découper le fichier binaire
 *
 *  @author       Ianis Graton
 *  @author       STS IRIS, Lycée Nicolas APPERT, ORVAULT (FRANCE)
 *  @since        2012-03-15
 *  @version      0.1
 *  @date         2012-04-16
 *
 *  Fabrication   DevCPP, projet   .dev
 *
 *  @todo         Liste des choses restant à faire.
 *
 *  @bug          <date du bug> - <CORRIGE> - <Intitulé précis du bug>
 */
//------------------------------------------------------------------------------
#ifndef ECHANGEBINAIRE_CPP
#define ECHANGEBINAIRE_CPP

// En-têtes standards necessaires dans ce fichier ------------------------------
#include <string>
using namespace std;

// En-tête propre à l'objet ----------------------------------------------------
#include "EchangeBinaire.h"

//------------------------------------------------------------------------------

EchangeBinaire::EchangeBinaire()
{
    this->qContenuCommandeBin = "";
    this->qContenuReponseBin = "";
    this->qTrameEchange = "";
    this->qTemps = "";
}

//---------------------------------------------------------------------------
// METHODES PUBLIQUES
//---------------------------------------------------------------------------

/** Description détaillée de la méthode DecouperEchange(QString qLigne)
 *  @pre    Description des préconditions nécessaires à la méthode
 *  @post   Description des postconditions nécessaires à la méthode
 *  @param
 *  @return
 *  @test   Voir la procédure dans le fichier associé.
 */
void EchangeBinaire::DecouperEchange(QString qLigne)
{
    this->qTemps = qLigne.left(5);

    if(qLigne[5] == 0x00)
    {
        this->qContenuCommandeBin = qLigne;
    }
    else
    {
        this->qContenuReponseBin = qLigne;
    }

    this->qTrameEchange = this->qContenuCommandeBin + this->qContenuReponseBin;
}

QString EchangeBinaire::getContenuCommandeBin()const
{
    return qContenuCommandeBin;
}

QString EchangeBinaire::getContenuReponseBin()const
{
    return qContenuReponseBin;
}

QString EchangeBinaire::getTrameEchange()const
{
    return qTrameEchange;
}

QString EchangeBinaire::getTemps()const
{
    return qTemps;
}



//------------------------------------------------------------------------------
#endif  //ECHANGEBINAIRE_CPP
//------------------------------------------------------------------------------
