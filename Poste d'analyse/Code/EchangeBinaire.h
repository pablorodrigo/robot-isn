// EchangeBinaire.h 0.1	<2012-03-15>	<Ianis Graton>

//------------------------------------------------------------------------------
#ifndef ECHANGEBINAIRE_H
#define ECHANGEBINAIRE_H
//------------------------------------------------------------------------------

// En-têtes standards necessaires dans ce fichier ------------------------------
#include <QString>


/** @brief Description rapide de EchangeBinaire
 *
 */
class EchangeBinaire
{
public:

    // METHODES -----------------------------------------------------------------

    // CONSTRUCTEUR
    //! Description rapide de la méthode
    EchangeBinaire();

    //! Description rapide de la méthode
    void DecouperEchange(QString qLigne);

    //! Description rapide de la méthode
    QString getContenuCommandeBin() const;

    //! Description rapide de la méthode
    QString getContenuReponseBin() const;

    //! Description rapide de la méthode
    QString getTrameEchange() const;

    //! Description rapide de la méthode
    QString getTemps() const;

    char* getHeure();
    char* getMinute();
    char* getSeconde();
    char* getMilliseconde();


private:

    // ATTRIBUTS ----------------------------------------------------------------

    QString qContenuCommandeBin;
    QString qContenuReponseBin;
    QString qTrameEchange;
    QString qTemps;
};

//------------------------------------------------------------------------------
#endif // ECHANGEBINAIRE_H
//------------------------------------------------------------------------------
