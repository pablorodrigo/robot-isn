// MainWindow.h	0.1	<2012-01-03>	<Ianis Graton>

//------------------------------------------------------------------------------
#ifndef MAINWINDOW_H
#define MAINWINDOW_H
//------------------------------------------------------------------------------

// En-têtes standards necessaires dans ce fichier ------------------------------
#include <QMainWindow>
#include <QtGui>
#include <vector>

// En-têtes propres à l'application necessaires dans ce fichier en-tete --------
#include "EchangeAscii.h"
#include "EchangeBinaire.h"


namespace Ui
{
class MainWindow;
}

/** @brief Description rapide de MainWindow
 *
 *  Description détaillée de  MainWindow.
 *  Description détaillée de  MainWindow.
 *  <p>
 *  Autre bloc de description détaillée de  MainWindow.
 *  Autre bloc de description détaillée de  MainWindow.
 */
class MainWindow : public QMainWindow
{
    Q_OBJECT

public:

    // METHODES -----------------------------------------------------------------

    // CONSTRUCTEURs et DESTRUCTEUR
    //! Description rapide de la méthode
    explicit MainWindow(QWidget *parent = 0);
    //! Description rapide de la méthode
    ~MainWindow();

    //! Description rapide de la méthode
    void construireArborescence();

    //! Description rapide de la méthode
    void parcourirFichierAscii();

    void remplirFenetreCouleurs();


private slots:

    // METHODES -----------------------------------------------------------------

    //! Description rapide de la méthode
    void on_actionOuvrir_triggered();

    //! Description rapide de la méthode
    void on_actionEnregistrer_triggered();

    //! Description rapide de la méthode
    void on_actionEnregistrer_Sous_triggered();

    //! Description rapide de la méthode
    void on_actionCopier_triggered();

    //! Description rapide de la méthode
    void on_actionColler_triggered();

    //! Description rapide de la méthode
    void on_actionCouper_triggered();

    //! Description rapide de la méthode
    void on_treeWidget_itemDoubleClicked(QTreeWidgetItem *item, int column);

    void on_actionQuitter_triggered();

    void on_actionA_propos_triggered();

    //void on_CouleurGraph_clicked();

    void on_CouleurGraph_clicked(bool checked);

private:
    // ATTRIBUTS ----------------------------------------------------------------

    Ui::MainWindow *ui;
    QFile *fichierAscii;
    QFile *fichierBinaire;
    QVector<EchangeAscii> vEchangesAscii;
    QVector<EchangeBinaire> vEchangesBinaire;

};

//------------------------------------------------------------------------------
#endif  //MAINWINDOW_H
//------------------------------------------------------------------------------

