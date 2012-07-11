//------------------------------------------------------------------------------
/** @file         EchangeAscii.cpp
 *  @brief        Le fichier EchangeAscii.cpp permet de découper le fichier ASCII
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
#ifndef ECHANGEASCII_CPP
#define ECHANGEASCII_CPP

// En-têtes standards necessaires dans ce fichier ------------------------------
#include <string>
using namespace std;

// En-tête propre à l'objet ----------------------------------------------------
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

/** Description détaillée de la méthode DecouperLigne(QString qLigne)
 *  @pre    Description des préconditions nécessaires à la méthode
 *  @post   Description des postconditions nécessaires à la méthode
 *  @param
 *  @return
 *  @test   Voir la procédure dans le fichier associé.
 */
void EchangeAscii::DecouperLigne(QString qLigne)
{
    unsigned int nFinTemps = qLigne.indexOf("_");

    if ((qLigne.contains("commande", Qt::CaseInsensitive)))
    {
        /** @brief Détermination du numéro de la commande*/
        unsigned int nDebutNumeroEchange = qLigne.indexOf("commande")+8;
        unsigned int nFinNumeroEchange = (qLigne.indexOf("="))-1;
        int nTailleLigne = qLigne.size();
        unsigned int nTailleNumeroEchange = nTailleLigne-(nDebutNumeroEchange+(nTailleLigne-nFinNumeroEchange));
        string sLigne = qLigne.toStdString();
        string sNumeroEchange = sLigne.substr(nDebutNumeroEchange, nTailleNumeroEchange);
        this->qNumeroEchange = sNumeroEchange.c_str();

        /** @brief Détermination du temps de la commande*/
        this->qTempsCommande = qLigne.toStdString().substr(0, nFinTemps).c_str();

        /** @brief Détermination du contenu de la commande*/
        this->qContenuCommande = qLigne.toStdString().substr(qLigne.indexOf("=")+2).c_str();
    }
    else if ((qLigne.contains("reponse", Qt::CaseInsensitive)))
    {
        /** @brief Détermination du numéro de la réponse*/
        unsigned int nDebutNumeroEchange = qLigne.indexOf("reponse")+7;
        unsigned int nFinNumeroEchange = (qLigne.indexOf("="))-1;
        int nTailleLigne = qLigne.size();
        unsigned int nTailleNumeroEchange = nTailleLigne-(nDebutNumeroEchange+(nTailleLigne-nFinNumeroEchange));
        string sLigne = qLigne.toStdString();
        string sNumeroEchange = sLigne.substr(nDebutNumeroEchange, nTailleNumeroEchange);
        this->qNumeroEchange = sNumeroEchange.c_str();

        /** @brief Détermination du temps de la réponse*/
        this->qTempsReponse = qLigne.toStdString().substr(0, nFinTemps).c_str();

        /** @brief Détermination du contenu de la réponse*/
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
