//------------------------------------------------------------------------------
/** @file         MainWindow.cpp
 *  @brief        Le fichier MainWindow.cpp cr�� les fonctionnalit�s de l'appliction
 *
 *  @author       Ianis Graton
 *  @author       STS IRIS, Lyc�e Nicolas APPERT, ORVAULT (FRANCE)
 *  @since        2012-01-03
 *  @version      0.1
 *  @date         2012-04-16
 *
 *  Fabrication   DevCPP, projet   .dev
 *
 *  @todo         Coder la m�thode permettant de d�couper un fichier binaire
 *                Coder les boutons permettant de mettre en �vidance par la couleur
 *                Coder l'affichage dans la fen�tre graphique
 *
 *  @bug          <date du bug> - <CORRIGE> - <Intitul� pr�cis du bug>
 */
//------------------------------------------------------------------------------
#ifndef MAINWINDOW_CPP
#define MAINWINDOW_CPP

// En-t�tes standards necessaires dans ce fichier ------------------------------
#include <iostream>
#include <sstream>
#include <fstream>
#include <QFile>
#include <string>
#include <stdlib.h>
#include <vector>
using namespace std;

// En-t�te propre � l'application ----------------------------------------------
#include "EchangeAscii.h"
#include "EchangeBinaire.h"

// En-t�te propre � l'objet ----------------------------------------------------
#include "mainwindow.h"
#include "ui_mainwindow.h"


//------------------------------------------------------------------------------

/** Description d�taill�e du CONSTRUCTEUR
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param
 *  @retval Valeurs de retour
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent), ui(new Ui::MainWindow)
{

    ui->setupUi(this);
    ui->BarreStatut->showMessage(tr("Bienvenue sur le poste d'analyse."), 10000);

    /** @brief un splash pour patienter pendant le chargement */
    /*QPixmap pixmap("nxt.jpg");
    QSplashScreen* splash = new QSplashScreen(pixmap, Qt::WindowStaysOnTopHint);
    splash->setMask( pixmap.mask() );
    QTimer::singleShot(3000, splash, SLOT(close()));
    splash->show();*/


}

/** Description d�taill�e du DESTRUCTEUR
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param
 *  @retval Valeurs de retour
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
MainWindow::~MainWindow()
{
    delete ui;
}

//---------------------------------------------------------------------------
// METHODES PUBLIQUES
//---------------------------------------------------------------------------

/** construireArborescence()permet de r�cup�rer une session, de regrouper une
 *  commande et sa r�ponse associ�, et de cr�er l'arborescence avec tous les
 *  �changes contenus dans le fichier.
 *  @pre    Le dossier ouvert doit contenir un fichier binaire et fichier ascii
 *          du m�me nom que le dossier avec les extensions .bin et .asc.
 *          Les fichier doivent �tre du m�me format.
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param  nomObjet
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void MainWindow::construireArborescence()
{

    /** @brief R�cup�ration de la session*/
    fstream     inFichierAscii ;
    fstream     inFichierBinaire;
    QFileDialog qDialogueFichier;
    string      sNomSession;
    QString     NomDossier = qDialogueFichier.getExistingDirectory(this, "Selectionner un repertoire", QString());
    string      sNomDossier = NomDossier.toStdString();
    size_t      indexDernierSlash = sNomDossier.find_last_of("/");
    sNomSession = sNomDossier.substr(indexDernierSlash+1);

    /** @brief Ajout d'item dans l'arborescence*/
    QString NomSession = sNomSession.c_str();
    QTreeWidgetItem *itemRacine = new QTreeWidgetItem;
    itemRacine->setText(0, NomSession);
    ui->treeWidget->insertTopLevelItem(0,itemRacine);

    QString NomFichierAscii = (sNomDossier+"/"+sNomSession+".txt").c_str();
    QString NomFichierBinaire = (sNomDossier+"/"+sNomSession+".bin").c_str();
    qDialogueFichier.setOption(QFileDialog::ShowDirsOnly);
    cout << endl << endl<< endl << sNomDossier <<endl<<endl<< sNomSession <<endl;
    fichierAscii = new QFile(NomFichierAscii);
    fichierBinaire = new QFile(NomFichierBinaire);

    inFichierAscii.open(NomFichierAscii.toStdString().c_str(), ios::in) ;
    inFichierBinaire.open(NomFichierBinaire.toStdString().c_str(), ios::in | ios::binary) ;

    QString buffer;
    if (inFichierAscii.is_open() && inFichierBinaire.is_open())
    {
        while(!inFichierAscii.eof())
        {

            ostringstream oss;
            oss << " "<<hex <<inFichierAscii.get();
            string s = oss.str();

            buffer += s.c_str();

        }
        ui->TxtRepBin->setText(buffer);
        inFichierAscii.close() ;
    }

    if(fichierAscii->open(QIODevice::ReadOnly | QIODevice::Text))
    {
        vEchangesAscii.clear();
        QTextStream fichier(fichierAscii);

        QString qEchange;
        unsigned int nNumeroEchange (0);
        unsigned int nPaireCommande (0);
        //QString qNumeroEchange;
        QTreeWidgetItem *itemEchange;

        itemEchange = new QTreeWidgetItem;
        itemEchange->setText(0, "Echange 1");
        itemRacine->addChild(itemEchange);

        EchangeAscii *mEchangeAscii;
        mEchangeAscii = new EchangeAscii();

        while(!fichier.atEnd())
        {
            QString qLigne = fichier.readLine();
            mEchangeAscii->DecouperLigne(qLigne);

            if ((qLigne.contains("commande", Qt::CaseInsensitive)))
            {
                qEchange = "Commande ";
            }
            else if ((qLigne.contains("reponse", Qt::CaseInsensitive)))
            {
                qEchange = "R�ponse ";
            }

            //unsigned int nBufferNumeroEchange = nNumeroEchange;
            if (nPaireCommande == 2)
            {
                vEchangesAscii.push_back(*mEchangeAscii);
                itemEchange = new QTreeWidgetItem;
                itemEchange->setText(0, "Echange "+vEchangesAscii[nNumeroEchange].getNumeroEchange());
                itemRacine->addChild(itemEchange);
                nNumeroEchange++;
                nPaireCommande = 0;
            }

            /*QTreeWidgetItem *itemCommande = new QTreeWidgetItem;
            itemCommande->setText(0, qEchange+QString::number(nNumeroEchange+1));
            itemEchange->addChild(itemCommande);*/

            nPaireCommande++;
        }

        fichierAscii->close();
    }

    if (fichierBinaire->open(QIODevice::ReadOnly | QIODevice::Text))
    {
        //QTextStream fichier(fichierBinaire);
        /*QString qs = ts.readAll();
        const char* qc = qs.toStdString().c_str();
        const QChar cqc;
        for(register int i=0; i<qs.size(); i++)
        {

        }
        cqc.toAscii();
        QByteArray fichier(qs.setRawData(qs.toStdString(), qs.length()));

        QByteArray fichier(fichierBinaire);

        int i(0);
        do
        {
            QByteArray ligne;

            ligne.append(fichier.at(i));
            if (fichier.at(i) == 0x0D)
            {
                if (fichier.at(i+1) == 0x0A)
                {
                    ligne.append(fichier.at(i+1));
                    mEchangeBinaire->DecouperEchange(ligne);
                    i += 2;
                }
            }

            vEchangesBinaire.push_back(*mEchangeBinaire);

            i++;
        }
        while(i<fichier.length());*/

        vEchangesBinaire.clear();
        EchangeBinaire *mEchangeBinaire;
        mEchangeBinaire = new EchangeBinaire();
        QTextStream fichier(fichierBinaire);
        unsigned int nEchange(0);

        do
        {
            QString qLigne = fichier.readLine();

            if (qLigne.contains(0x0D))
            {
                if (qLigne.contains(0x0A))
                {
                    mEchangeBinaire->DecouperEchange(qLigne);
                    nEchange++;
                }
            }

            if (nEchange == 2)
            {
                vEchangesBinaire.push_back(*mEchangeBinaire);
                nEchange = 0;

            }
        }
        while(!fichier.atEnd());
    }
    fichierBinaire->close();
}

/** Description d�taill�e de la m�thode parcourirFichierAscii()
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param  nomObjet
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void MainWindow::parcourirFichierAscii()
{

}

//---------------------------------------------------------------------------
// METHODES PRIVEES
//---------------------------------------------------------------------------

void MainWindow::on_actionOuvrir_triggered()
{
    /** @brief Appel de la m�thode construireArborescence()*/
    this->construireArborescence();
}

/** Description d�taill�e de la m�thode on_actionEnregistrer_triggered()
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param  nomObjet
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void MainWindow::on_actionEnregistrer_triggered()
{
    on_actionEnregistrer_Sous_triggered();
}

/** Description d�taill�e de la m�thode on_actionEnregistrer_Sous_triggered()
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param  nomObjet
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void MainWindow::on_actionEnregistrer_Sous_triggered()
{

    QString texteEnHtml = ui->TxtComBin->toHtml();
    QString NomFichier = QFileDialog::getSaveFileName(this, "Enregistrer...", "Sans titre.txt");

    QFile fichier(NomFichier);
    if (fichier.open(QIODevice::ReadWrite))
    {
        QTextStream out(&fichier);
        out << texteEnHtml;
        ui->BarreStatut->showMessage(tr("Enregistrement de ")+ NomFichier,5000);
    }
    else
    {
        QMessageBox::warning(this,tr("Erreur lors de la sauvegarde"),tr("Le document n'a pas pu �tre sauvegard�"));

    }
}

/** Description d�taill�e de la m�thode on_actionCopier_triggered()
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param  nomObjet
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void MainWindow::on_actionCopier_triggered()
{
    ui->TxtComBin->copy();
    ui->TxtComTxt->copy();
    ui->TxtRepBin->copy();
    ui->TxtRepTxt->copy();
}

/** Description d�taill�e de la m�thode on_actionColler_triggered()
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param  nomObjet
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void MainWindow::on_actionColler_triggered()
{
    ui->TxtComBin->paste();
    ui->TxtComTxt->paste();
    ui->TxtRepBin->paste();
    ui->TxtRepTxt->paste();
}

/** Description d�taill�e de la m�thode on_actionCouper_triggered()
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param  nomObjet
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void MainWindow::on_actionCouper_triggered()
{
    ui->TxtComBin->cut();
    ui->TxtComTxt->cut();
    ui->TxtRepBin->cut();
    ui->TxtRepTxt->cut();
}

/** Description d�taill�e de la m�thode on_actionCouper_triggered()
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param  nomObjet
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void MainWindow::on_actionQuitter_triggered()
{
    exit(0);
}

void MainWindow::on_actionA_propos_triggered()
{
    QMessageBox::about(this,"A propos ...","<p><b>Poste d'analyse</b></p>"
                       "<p>R�alis� en 2012</p>"
                       "Programm� par: Ianis Graton");
}



/** Description d�taill�e de la m�thode on_treeWidget_itemDoubleClicked(QTreeWidgetItem *item, int column)
 *  @pre    Description des pr�conditions n�cessaires � la m�thode
 *  @post   Description des postconditions n�cessaires � la m�thode
 *  @param  nomObjet
 *  @return
 *  @test   Voir la proc�dure dans le fichier associ�.
 */
void MainWindow::on_treeWidget_itemDoubleClicked(QTreeWidgetItem *item, int column)
{
    QString qTitre = item->text(column);
    QString qType;
    QString qNumeroEchange;

    if ( !(qTitre.startsWith("Commande")||qTitre.startsWith("R�ponse")) )
    {
        unsigned int nDebutNumeroEchange = qTitre.indexOf("Commande")+9;
        string sLigne = qTitre.toStdString();
        string sNumeroEchange = sLigne.substr(nDebutNumeroEchange);
        qNumeroEchange = sNumeroEchange.c_str();

        QString qCommande = vEchangesAscii.at(qNumeroEchange.toInt()).getContenuCommande();
        QString qReponse = vEchangesAscii.at(qNumeroEchange.toInt()).getContenuReponse();

        //QString qTemps = vEchangesBinaire.at(qNumeroEchange.toInt()).getTemps();


        if (qTitre.startsWith("Echange"))
        {
            ui->NomComTxt->setText("Commande " + qNumeroEchange) ;
            ui->NomRepTxt->setText("R�ponse " + qNumeroEchange) ;
            ui->NomEchange->setText("Echange " + qNumeroEchange) ;
            ui->NomComBin->setText("Commande " + qNumeroEchange) ;
            ui->NomRepBin->setText("R�ponse " + qNumeroEchange) ;

            ui->MessageBluetooth->setText(qCommande);

            ui->TxtComTxt->setText(qCommande);
            ui->TxtRepTxt->setText(qReponse);
            ui->Tableau->setItem(0,0,new QTableWidgetItem(qReponse));

        }
    }
}

void MainWindow::on_CouleurGraph_clicked(bool checked)
{
    QPalette palette ;

    if(checked==true)
    {
        palette.setColor( QPalette::WindowText, Qt::red ) ;
        ui->MessageBluetooth->setPalette( palette ) ;
    }
    else
    {
        palette.setColor( QPalette::WindowText, Qt::black ) ;
        ui->MessageBluetooth->setPalette( palette ) ;
    }
}

void MainWindow::remplirFenetreCouleurs()
{

}

//---------------------------------------------------------------------------
#endif  // MAINWINDOW_CPP
//---------------------------------------------------------------------------





