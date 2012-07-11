//------------------------------------------------------------------------------
/** @file         EchangeAscii.cpp
 *  @brief        Le fichier EchangeAscii.cpp permet de d�couper le fichier ASCII
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
#ifndef ECHANGEASCII_CPP
#define ECHANGEASCII_CPP

// En-t�tes standards necessaires dans ce fichier ------------------------------
#include <string>
using namespace std;

// En-t�te propre � l'objet ----------------------------------------------------
#include "EchangeAscii.h"

//------------------------------------------------------------------------------

EchangeAscii::EchangeAscii()
{
    this->qLigneCommande = "";
    this->qLigneReponse = "";
    this->qTempsCommande = "";
    this->qTempsReponse = "";
    this->qNumeroEchange = "";
    this->qContenuCommande = "";
    this->qContenuReponse = "";
}

//---------------------------------------------------------------------------
// METHODES PUBLIQUES
//---------------------------------------------------------------------------

/** Description d�taill�e de la m�thode DecouperLigne(QString qLigne)
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void EchangeAscii::DecouperLigne(QString qLigne)
{
    unsigned int nFinTemps = qLigne.indexOf("_");

    if ((qLigne.contains("commande", Qt::CaseInsensitive)))
    {
        /** @brief D�termination du num�ro de la commande*/
        unsigned int nDebutNumeroEchange = qLigne.indexOf("commande")+8;
        unsigned int nFinNumeroEchange = (qLigne.indexOf("="))-1;
        int nTailleLigne = qLigne.size();
        unsigned int nTailleNumeroEchange = nTailleLigne-(nDebutNumeroEchange+(nTailleLigne-nFinNumeroEchange));
        string sLigne = qLigne.toStdString();
        string sNumeroEchange = sLigne.substr(nDebutNumeroEchange, nTailleNumeroEchange);
        this->qNumeroEchange = sNumeroEchange.c_str();

        /** @brief D�termination du temps de la commande*/
        this->qTempsCommande = qLigne.toStdString().substr(0, nFinTemps).c_str();

        /** @brief D�termination du contenu de la commande*/
        this->qContenuCommande = qLigne.toStdString().substr(qLigne.indexOf("=")+2).c_str();
    }
    else if ((qLigne.contains("reponse", Qt::CaseInsensitive)))
    {
        /** @brief D�termination du num�ro de la r�ponse*/
        unsigned int nDebutNumeroEchange = qLigne.indexOf("reponse")+7;
        unsigned int nFinNumeroEchange = (qLigne.indexOf("="))-1;
        int nTailleLigne = qLigne.size();
        unsigned int nTailleNumeroEchange = nTailleLigne-(nDebutNumeroEchange+(nTailleLigne-nFinNumeroEchange));
        string sLigne = qLigne.toStdString();
        string sNumeroEchange = sLigne.substr(nDebutNumeroEchange, nTailleNumeroEchange);
        this->qNumeroEchange = sNumeroEchange.c_str();

        /** @brief D�termination du temps de la r�ponse*/
        this->qTempsReponse = qLigne.toStdString().substr(0, nFinTemps).c_str();

        /** @brief D�termination du contenu de la r�ponse*/
        this->qContenuReponse = qLigne.toStdString().substr(qLigne.indexOf("=")+2).c_str();
    }
}

QString EchangeAscii::getLigneCommande() const
{
    return this->qLigneCommande;
}

QString EchangeAscii::getLigneReponse()const
{
    return this->qLigneReponse;
}

QString EchangeAscii::getTempsCommande()const
{
    return qTempsCommande;
}

QString EchangeAscii::getTempsReponse()const
{
    return qTempsReponse;
}

QString EchangeAscii::getContenuCommande()const
{
    return qContenuCommande;
}

QString EchangeAscii::getContenuReponse()const
{
    return qContenuReponse;
}

QString EchangeAscii::getNumeroEchange()const
{
    return qNumeroEchange;
}


//------------------------------------------------------------------------------
#endif  //ECHANGEASCII_CPP
//------------------------------------------------------------------------------
