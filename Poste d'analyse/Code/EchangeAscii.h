// EchangeAscii.h 0.1	<2012-03-15>	<Ianis Graton>

//------------------------------------------------------------------------------
#ifndef ECHANGEASCII_H
#define ECHANGEASCII_H
//------------------------------------------------------------------------------

// En-têtes standards necessaires dans ce fichier ------------------------------
#include <QString>


/** @brief Description rapide de EchangeAscii
 *
 *  Description détaillée de  EchangeAscii.
 *  Description détaillée de  EchangeAscii.
 *  <p>
 *  Autre bloc de description détaillée de  EchangeAscii.
 *  Autre bloc de description détaillée de  EchangeAscii.
 */
class EchangeAscii
{
public:

    // METHODES -----------------------------------------------------------------

    // CONSTRUCTEUR
    //! Description rapide de la méthode
    EchangeAscii();

    //! Description rapide de la méthode
    void setCommande(QString qLigne);

    //! Description rapide de la méthode
    void setReponse(QString qReponse);

    //! Description rapide de la méthode
    void DecouperLigne(QString qLigne);

    //! Description rapide de la méthode
    QString getLigneCommande() const;

    //! Description rapide de la méthode
    QString getLigneReponse() const;

    //! Description rapide de la méthode
    QString getTempsCommande() const;

    //! Description rapide de la méthode
    QString getTempsReponse() const;

    //! Description rapide de la méthode
    QString getNumeroEchange() const;

    //! Description rapide de la méthode
    QString getContenuCommande() const;

    //! Description rapide de la méthode
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
