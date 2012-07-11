// EchangeAscii.h 0.1	<2012-03-15>	<Ianis Graton>

//------------------------------------------------------------------------------
#ifndef ECHANGEASCII_H
#define ECHANGEASCII_H
//------------------------------------------------------------------------------

// En-t�tes standards necessaires dans ce fichier ------------------------------
#include <QString>


/** @brief Description rapide de EchangeAscii
 *
 *  Description d�taill�e de  EchangeAscii.
 *  Description d�taill�e de  EchangeAscii.
 *  <p>
 *  Autre bloc de description d�taill�e de  EchangeAscii.
 *  Autre bloc de description d�taill�e de  EchangeAscii.
 */
class EchangeAscii
{
public:

    // METHODES -----------------------------------------------------------------

    // CONSTRUCTEUR
    //! Description rapide de la m�thode
    EchangeAscii();

    //! Description rapide de la m�thode
    void setCommande(QString qLigne);

    //! Description rapide de la m�thode
    void setReponse(QString qReponse);

    //! Description rapide de la m�thode
    void DecouperLigne(QString qLigne);

    //! Description rapide de la m�thode
    QString getLigneCommande() const;

    //! Description rapide de la m�thode
    QString getLigneReponse() const;

    //! Description rapide de la m�thode
    QString getTempsCommande() const;

    //! Description rapide de la m�thode
    QString getTempsReponse() const;

    //! Description rapide de la m�thode
    QString getNumeroEchange() const;

    //! Description rapide de la m�thode
    QString getContenuCommande() const;

    //! Description rapide de la m�thode
    QString getContenuReponse() const;


private:
    // ATTRIBUTS ----------------------------------------------------------------
    QString qLigneCommande;
    QString qLigneReponse;
    QString qTempsCommande;
    QString qTempsReponse;
    QString qNumeroEchange;
    QString qContenuCommande;
    QString qContenuReponse;

};

//------------------------------------------------------------------------------
#endif  //ECHANGEASCII_H
//------------------------------------------------------------------------------
