//------------------------------------------------------------------------------
/** @file         EchangeBinaire.cpp
 *  @brief        Le fichier EchangeBinaire.cpp permet de d�couper le fichier binaire
 *
 *  @author       Ianis Graton
 *  @author       STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
 *  @since        2012-03-15
 *  @version      0.1
 *  @date         2012-04-16
 *
 *  Fabrication   DevCPP, projet   .dev
 *
 *  @todo         Liste des choses restant � faire.
 *
 *  @bug          <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
 */
//------------------------------------------------------------------------------
#ifndef ECHANGEBINAIRE_CPP
#define ECHANGEBINAIRE_CPP

// En-t�tes standards necessaires dans ce fichier ------------------------------
#include <string>
using namespace std;

// En-t�te propre � l'objet ----------------------------------------------------
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

/** Description d�taill�e de la m�thode DecouperEchange(QString qLigne)
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
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
