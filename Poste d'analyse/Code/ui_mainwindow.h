/********************************************************************************
** Form generated from reading UI file 'mainwindow.ui'
**
** Created: Tue 12. Jun 15:45:06 2012
**      by: Qt User Interface Compiler version 4.7.3
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_MAINWINDOW_H
#define UI_MAINWINDOW_H

#include <QtCore/QVariant>
#include <QtGui/QAction>
#include <QtGui/QApplication>
#include <QtGui/QButtonGroup>
#include <QtGui/QGridLayout>
#include <QtGui/QHeaderView>
#include <QtGui/QLabel>
#include <QtGui/QMainWindow>
#include <QtGui/QMenu>
#include <QtGui/QMenuBar>
#include <QtGui/QPushButton>
#include <QtGui/QSpacerItem>
#include <QtGui/QStatusBar>
#include <QtGui/QTabWidget>
#include <QtGui/QTableWidget>
#include <QtGui/QTextEdit>
#include <QtGui/QToolBar>
#include <QtGui/QTreeWidget>
#include <QtGui/QVBoxLayout>
#include <QtGui/QWidget>

QT_BEGIN_NAMESPACE

class Ui_MainWindow
{
public:
    QAction *actionOuvrir;
    QAction *actionEnregistrer;
    QAction *actionCopier;
    QAction *actionColler;
    QAction *actionCouper;
    QAction *actionEnregistrer_Sous;
    QAction *actionQuitter;
    QAction *actionA_propos;
    QWidget *centralWidget;
    QGridLayout *gridLayout;
    QTreeWidget *treeWidget;
    QTabWidget *tabWidget;
    QWidget *tab_bin;
    QVBoxLayout *verticalLayout_5;
    QVBoxLayout *verticalLayout_4;
    QLabel *NomComBin;
    QTextEdit *TxtComBin;
    QSpacerItem *verticalSpacer_7;
    QLabel *NomRepBin;
    QTextEdit *TxtRepBin;
    QSpacerItem *verticalSpacer_8;
    QWidget *tab_txt;
    QVBoxLayout *verticalLayout_3;
    QVBoxLayout *verticalLayout_2;
    QLabel *NomComTxt;
    QTextEdit *TxtComTxt;
    QSpacerItem *verticalSpacer_5;
    QLabel *NomRepTxt;
    QTextEdit *TxtRepTxt;
    QSpacerItem *verticalSpacer_6;
    QWidget *tab_graph;
    QVBoxLayout *verticalLayout;
    QLabel *NomEchange;
    QTableWidget *Tableau;
    QSpacerItem *verticalSpacer;
    QLabel *label_9;
    QLabel *MessageBluetooth;
    QSpacerItem *verticalSpacer_2;
    QPushButton *CouleurGraph;
    QMenuBar *menuBar;
    QMenu *menuFichier;
    QMenu *menuEdition;
    QMenu *menuAide;
    QToolBar *toolBar;
    QStatusBar *BarreStatut;

    void setupUi(QMainWindow *MainWindow)
    {
        if (MainWindow->objectName().isEmpty())
            MainWindow->setObjectName(QString::fromUtf8("MainWindow"));
        MainWindow->resize(988, 685);
        MainWindow->setMaximumSize(QSize(16777215, 16777215));
        MainWindow->setUnifiedTitleAndToolBarOnMac(false);
        actionOuvrir = new QAction(MainWindow);
        actionOuvrir->setObjectName(QString::fromUtf8("actionOuvrir"));
        actionEnregistrer = new QAction(MainWindow);
        actionEnregistrer->setObjectName(QString::fromUtf8("actionEnregistrer"));
        actionCopier = new QAction(MainWindow);
        actionCopier->setObjectName(QString::fromUtf8("actionCopier"));
        actionColler = new QAction(MainWindow);
        actionColler->setObjectName(QString::fromUtf8("actionColler"));
        actionCouper = new QAction(MainWindow);
        actionCouper->setObjectName(QString::fromUtf8("actionCouper"));
        actionEnregistrer_Sous = new QAction(MainWindow);
        actionEnregistrer_Sous->setObjectName(QString::fromUtf8("actionEnregistrer_Sous"));
        actionQuitter = new QAction(MainWindow);
        actionQuitter->setObjectName(QString::fromUtf8("actionQuitter"));
        actionA_propos = new QAction(MainWindow);
        actionA_propos->setObjectName(QString::fromUtf8("actionA_propos"));
        centralWidget = new QWidget(MainWindow);
        centralWidget->setObjectName(QString::fromUtf8("centralWidget"));
        gridLayout = new QGridLayout(centralWidget);
        gridLayout->setSpacing(6);
        gridLayout->setContentsMargins(11, 11, 11, 11);
        gridLayout->setObjectName(QString::fromUtf8("gridLayout"));
        treeWidget = new QTreeWidget(centralWidget);
        QFont font;
        font.setPointSize(10);
        QTreeWidgetItem *__qtreewidgetitem = new QTreeWidgetItem();
        __qtreewidgetitem->setText(0, QString::fromUtf8("Liste des \303\251changes"));
        __qtreewidgetitem->setFont(0, font);
        treeWidget->setHeaderItem(__qtreewidgetitem);
        treeWidget->setObjectName(QString::fromUtf8("treeWidget"));
        treeWidget->setMaximumSize(QSize(16777215, 16777215));
        QFont font1;
        font1.setPointSize(10);
        font1.setBold(false);
        font1.setWeight(50);
        treeWidget->setFont(font1);
        treeWidget->setContextMenuPolicy(Qt::DefaultContextMenu);
        treeWidget->setAutoFillBackground(false);
        treeWidget->setStyleSheet(QString::fromUtf8("background-image: url(robot3.jpg);"));
        treeWidget->setVerticalScrollBarPolicy(Qt::ScrollBarAsNeeded);
        treeWidget->setHorizontalScrollBarPolicy(Qt::ScrollBarAsNeeded);
        treeWidget->setColumnCount(1);

        gridLayout->addWidget(treeWidget, 0, 0, 1, 1);

        tabWidget = new QTabWidget(centralWidget);
        tabWidget->setObjectName(QString::fromUtf8("tabWidget"));
        QSizePolicy sizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
        sizePolicy.setHorizontalStretch(0);
        sizePolicy.setVerticalStretch(0);
        sizePolicy.setHeightForWidth(tabWidget->sizePolicy().hasHeightForWidth());
        tabWidget->setSizePolicy(sizePolicy);
        tabWidget->setMinimumSize(QSize(0, 0));
        tabWidget->setMaximumSize(QSize(16777215, 16777215));
        tabWidget->setAutoFillBackground(false);
        tabWidget->setStyleSheet(QString::fromUtf8(""));
        tab_bin = new QWidget();
        tab_bin->setObjectName(QString::fromUtf8("tab_bin"));
        verticalLayout_5 = new QVBoxLayout(tab_bin);
        verticalLayout_5->setSpacing(6);
        verticalLayout_5->setContentsMargins(11, 11, 11, 11);
        verticalLayout_5->setObjectName(QString::fromUtf8("verticalLayout_5"));
        verticalLayout_4 = new QVBoxLayout();
        verticalLayout_4->setSpacing(6);
        verticalLayout_4->setObjectName(QString::fromUtf8("verticalLayout_4"));
        NomComBin = new QLabel(tab_bin);
        NomComBin->setObjectName(QString::fromUtf8("NomComBin"));
        QFont font2;
        font2.setPointSize(14);
        font2.setUnderline(true);
        NomComBin->setFont(font2);
        NomComBin->setAlignment(Qt::AlignCenter);

        verticalLayout_4->addWidget(NomComBin);

        TxtComBin = new QTextEdit(tab_bin);
        TxtComBin->setObjectName(QString::fromUtf8("TxtComBin"));
        TxtComBin->setMinimumSize(QSize(500, 0));
        QFont font3;
        font3.setPointSize(12);
        TxtComBin->setFont(font3);

        verticalLayout_4->addWidget(TxtComBin);

        verticalSpacer_7 = new QSpacerItem(20, 100, QSizePolicy::Minimum, QSizePolicy::Fixed);

        verticalLayout_4->addItem(verticalSpacer_7);

        NomRepBin = new QLabel(tab_bin);
        NomRepBin->setObjectName(QString::fromUtf8("NomRepBin"));
        NomRepBin->setFont(font2);
        NomRepBin->setAlignment(Qt::AlignCenter);

        verticalLayout_4->addWidget(NomRepBin);

        TxtRepBin = new QTextEdit(tab_bin);
        TxtRepBin->setObjectName(QString::fromUtf8("TxtRepBin"));
        TxtRepBin->setFont(font3);

        verticalLayout_4->addWidget(TxtRepBin);

        verticalSpacer_8 = new QSpacerItem(20, 20, QSizePolicy::Minimum, QSizePolicy::Fixed);

        verticalLayout_4->addItem(verticalSpacer_8);


        verticalLayout_5->addLayout(verticalLayout_4);

        tabWidget->addTab(tab_bin, QString());
        tab_txt = new QWidget();
        tab_txt->setObjectName(QString::fromUtf8("tab_txt"));
        verticalLayout_3 = new QVBoxLayout(tab_txt);
        verticalLayout_3->setSpacing(6);
        verticalLayout_3->setContentsMargins(11, 11, 11, 11);
        verticalLayout_3->setObjectName(QString::fromUtf8("verticalLayout_3"));
        verticalLayout_2 = new QVBoxLayout();
        verticalLayout_2->setSpacing(6);
        verticalLayout_2->setObjectName(QString::fromUtf8("verticalLayout_2"));
        NomComTxt = new QLabel(tab_txt);
        NomComTxt->setObjectName(QString::fromUtf8("NomComTxt"));
        NomComTxt->setFont(font2);
        NomComTxt->setAlignment(Qt::AlignCenter);

        verticalLayout_2->addWidget(NomComTxt);

        TxtComTxt = new QTextEdit(tab_txt);
        TxtComTxt->setObjectName(QString::fromUtf8("TxtComTxt"));
        TxtComTxt->setMinimumSize(QSize(500, 0));
        TxtComTxt->setFont(font3);

        verticalLayout_2->addWidget(TxtComTxt);

        verticalSpacer_5 = new QSpacerItem(20, 100, QSizePolicy::Minimum, QSizePolicy::Fixed);

        verticalLayout_2->addItem(verticalSpacer_5);

        NomRepTxt = new QLabel(tab_txt);
        NomRepTxt->setObjectName(QString::fromUtf8("NomRepTxt"));
        NomRepTxt->setFont(font2);
        NomRepTxt->setAlignment(Qt::AlignCenter);

        verticalLayout_2->addWidget(NomRepTxt);

        TxtRepTxt = new QTextEdit(tab_txt);
        TxtRepTxt->setObjectName(QString::fromUtf8("TxtRepTxt"));
        TxtRepTxt->setFont(font3);

        verticalLayout_2->addWidget(TxtRepTxt);

        verticalSpacer_6 = new QSpacerItem(20, 20, QSizePolicy::Minimum, QSizePolicy::Fixed);

        verticalLayout_2->addItem(verticalSpacer_6);


        verticalLayout_3->addLayout(verticalLayout_2);

        tabWidget->addTab(tab_txt, QString());
        tab_graph = new QWidget();
        tab_graph->setObjectName(QString::fromUtf8("tab_graph"));
        verticalLayout = new QVBoxLayout(tab_graph);
        verticalLayout->setSpacing(6);
        verticalLayout->setContentsMargins(11, 11, 11, 11);
        verticalLayout->setObjectName(QString::fromUtf8("verticalLayout"));
        NomEchange = new QLabel(tab_graph);
        NomEchange->setObjectName(QString::fromUtf8("NomEchange"));
        QFont font4;
        font4.setPointSize(14);
        font4.setUnderline(true);
        font4.setStyleStrategy(QFont::PreferDefault);
        NomEchange->setFont(font4);
        NomEchange->setTextFormat(Qt::AutoText);
        NomEchange->setAlignment(Qt::AlignCenter);

        verticalLayout->addWidget(NomEchange);

        Tableau = new QTableWidget(tab_graph);
        if (Tableau->columnCount() < 2)
            Tableau->setColumnCount(2);
        QFont font5;
        font5.setBold(true);
        font5.setWeight(75);
        QTableWidgetItem *__qtablewidgetitem = new QTableWidgetItem();
        __qtablewidgetitem->setFont(font5);
        Tableau->setHorizontalHeaderItem(0, __qtablewidgetitem);
        QFont font6;
        font6.setPointSize(8);
        font6.setBold(true);
        font6.setWeight(75);
        QTableWidgetItem *__qtablewidgetitem1 = new QTableWidgetItem();
        __qtablewidgetitem1->setFont(font6);
        Tableau->setHorizontalHeaderItem(1, __qtablewidgetitem1);
        if (Tableau->rowCount() < 2)
            Tableau->setRowCount(2);
        QTableWidgetItem *__qtablewidgetitem2 = new QTableWidgetItem();
        Tableau->setVerticalHeaderItem(0, __qtablewidgetitem2);
        QTableWidgetItem *__qtablewidgetitem3 = new QTableWidgetItem();
        Tableau->setVerticalHeaderItem(1, __qtablewidgetitem3);
        QTableWidgetItem *__qtablewidgetitem4 = new QTableWidgetItem();
        __qtablewidgetitem4->setFont(font);
        Tableau->setItem(0, 0, __qtablewidgetitem4);
        QTableWidgetItem *__qtablewidgetitem5 = new QTableWidgetItem();
        __qtablewidgetitem5->setFont(font);
        Tableau->setItem(0, 1, __qtablewidgetitem5);
        QTableWidgetItem *__qtablewidgetitem6 = new QTableWidgetItem();
        __qtablewidgetitem6->setFont(font);
        Tableau->setItem(1, 0, __qtablewidgetitem6);
        QTableWidgetItem *__qtablewidgetitem7 = new QTableWidgetItem();
        __qtablewidgetitem7->setFont(font);
        Tableau->setItem(1, 1, __qtablewidgetitem7);
        Tableau->setObjectName(QString::fromUtf8("Tableau"));
        Tableau->setMaximumSize(QSize(16777215, 110));
        Tableau->setFont(font6);
        Tableau->setVerticalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
        Tableau->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
        Tableau->setSelectionMode(QAbstractItemView::ExtendedSelection);
        Tableau->horizontalHeader()->setCascadingSectionResizes(true);
        Tableau->horizontalHeader()->setDefaultSectionSize(150);
        Tableau->horizontalHeader()->setStretchLastSection(true);
        Tableau->verticalHeader()->setCascadingSectionResizes(true);
        Tableau->verticalHeader()->setDefaultSectionSize(40);
        Tableau->verticalHeader()->setStretchLastSection(true);

        verticalLayout->addWidget(Tableau);

        verticalSpacer = new QSpacerItem(518, 100, QSizePolicy::Minimum, QSizePolicy::Fixed);

        verticalLayout->addItem(verticalSpacer);

        label_9 = new QLabel(tab_graph);
        label_9->setObjectName(QString::fromUtf8("label_9"));
        QFont font7;
        font7.setPointSize(14);
        font7.setBold(false);
        font7.setItalic(false);
        font7.setUnderline(true);
        font7.setWeight(50);
        label_9->setFont(font7);

        verticalLayout->addWidget(label_9);

        MessageBluetooth = new QLabel(tab_graph);
        MessageBluetooth->setObjectName(QString::fromUtf8("MessageBluetooth"));
        QSizePolicy sizePolicy1(QSizePolicy::Preferred, QSizePolicy::Preferred);
        sizePolicy1.setHorizontalStretch(0);
        sizePolicy1.setVerticalStretch(0);
        sizePolicy1.setHeightForWidth(MessageBluetooth->sizePolicy().hasHeightForWidth());
        MessageBluetooth->setSizePolicy(sizePolicy1);
        QFont font8;
        font8.setPointSize(12);
        font8.setBold(false);
        font8.setWeight(50);
        MessageBluetooth->setFont(font8);
        MessageBluetooth->setAutoFillBackground(false);
        MessageBluetooth->setFrameShape(QFrame::NoFrame);
        MessageBluetooth->setFrameShadow(QFrame::Plain);
        MessageBluetooth->setTextFormat(Qt::RichText);
        MessageBluetooth->setAlignment(Qt::AlignLeading|Qt::AlignLeft|Qt::AlignTop);

        verticalLayout->addWidget(MessageBluetooth);

        verticalSpacer_2 = new QSpacerItem(518, 40, QSizePolicy::Minimum, QSizePolicy::Fixed);

        verticalLayout->addItem(verticalSpacer_2);

        CouleurGraph = new QPushButton(tab_graph);
        CouleurGraph->setObjectName(QString::fromUtf8("CouleurGraph"));
        CouleurGraph->setCheckable(true);

        verticalLayout->addWidget(CouleurGraph);

        tabWidget->addTab(tab_graph, QString());

        gridLayout->addWidget(tabWidget, 0, 1, 1, 1);

        MainWindow->setCentralWidget(centralWidget);
        menuBar = new QMenuBar(MainWindow);
        menuBar->setObjectName(QString::fromUtf8("menuBar"));
        menuBar->setGeometry(QRect(0, 0, 988, 24));
        menuFichier = new QMenu(menuBar);
        menuFichier->setObjectName(QString::fromUtf8("menuFichier"));
        menuEdition = new QMenu(menuBar);
        menuEdition->setObjectName(QString::fromUtf8("menuEdition"));
        menuAide = new QMenu(menuBar);
        menuAide->setObjectName(QString::fromUtf8("menuAide"));
        MainWindow->setMenuBar(menuBar);
        toolBar = new QToolBar(MainWindow);
        toolBar->setObjectName(QString::fromUtf8("toolBar"));
        MainWindow->addToolBar(Qt::TopToolBarArea, toolBar);
        BarreStatut = new QStatusBar(MainWindow);
        BarreStatut->setObjectName(QString::fromUtf8("BarreStatut"));
        BarreStatut->setProperty("horloge", QVariant(QDateTime(QDate(2012, 2, 3), QTime(16, 22, 0))));
        MainWindow->setStatusBar(BarreStatut);

        menuBar->addAction(menuFichier->menuAction());
        menuBar->addAction(menuEdition->menuAction());
        menuBar->addAction(menuAide->menuAction());
        menuFichier->addAction(actionOuvrir);
        menuFichier->addAction(actionEnregistrer);
        menuFichier->addAction(actionEnregistrer_Sous);
        menuFichier->addAction(actionQuitter);
        menuEdition->addAction(actionCopier);
        menuEdition->addAction(actionColler);
        menuEdition->addAction(actionCouper);
        menuAide->addAction(actionA_propos);
        toolBar->addSeparator();

        retranslateUi(MainWindow);

        tabWidget->setCurrentIndex(0);


        QMetaObject::connectSlotsByName(MainWindow);
    } // setupUi

    void retranslateUi(QMainWindow *MainWindow)
    {
        MainWindow->setWindowTitle(QApplication::translate("MainWindow", "MainWindow", 0, QApplication::UnicodeUTF8));
        actionOuvrir->setText(QApplication::translate("MainWindow", "Ouvrir", 0, QApplication::UnicodeUTF8));
        actionOuvrir->setShortcut(QApplication::translate("MainWindow", "Ctrl+O", 0, QApplication::UnicodeUTF8));
        actionEnregistrer->setText(QApplication::translate("MainWindow", "Enregistrer", 0, QApplication::UnicodeUTF8));
        actionEnregistrer->setShortcut(QApplication::translate("MainWindow", "Ctrl+S", 0, QApplication::UnicodeUTF8));
        actionCopier->setText(QApplication::translate("MainWindow", "Copier", 0, QApplication::UnicodeUTF8));
        actionCopier->setShortcut(QApplication::translate("MainWindow", "Ctrl+C", 0, QApplication::UnicodeUTF8));
        actionColler->setText(QApplication::translate("MainWindow", "Coller", 0, QApplication::UnicodeUTF8));
        actionColler->setShortcut(QApplication::translate("MainWindow", "Ctrl+V", 0, QApplication::UnicodeUTF8));
        actionCouper->setText(QApplication::translate("MainWindow", "Couper", 0, QApplication::UnicodeUTF8));
        actionCouper->setShortcut(QApplication::translate("MainWindow", "Ctrl+X", 0, QApplication::UnicodeUTF8));
        actionEnregistrer_Sous->setText(QApplication::translate("MainWindow", "Enregistrer Sous...", 0, QApplication::UnicodeUTF8));
        actionQuitter->setText(QApplication::translate("MainWindow", "Quitter", 0, QApplication::UnicodeUTF8));
        actionA_propos->setText(QApplication::translate("MainWindow", "A propos", 0, QApplication::UnicodeUTF8));
        NomComBin->setText(QApplication::translate("MainWindow", "Commande", 0, QApplication::UnicodeUTF8));
        NomRepBin->setText(QApplication::translate("MainWindow", "R\303\251ponse", 0, QApplication::UnicodeUTF8));
        tabWidget->setTabText(tabWidget->indexOf(tab_bin), QApplication::translate("MainWindow", "binaire", 0, QApplication::UnicodeUTF8));
        NomComTxt->setText(QApplication::translate("MainWindow", "Commande", 0, QApplication::UnicodeUTF8));
        NomRepTxt->setText(QApplication::translate("MainWindow", "R\303\251ponse", 0, QApplication::UnicodeUTF8));
        tabWidget->setTabText(tabWidget->indexOf(tab_txt), QApplication::translate("MainWindow", "textuel", 0, QApplication::UnicodeUTF8));
        NomEchange->setText(QApplication::translate("MainWindow", "Echange", 0, QApplication::UnicodeUTF8));
        QTableWidgetItem *___qtablewidgetitem = Tableau->horizontalHeaderItem(0);
        ___qtablewidgetitem->setText(QApplication::translate("MainWindow", "Heure", 0, QApplication::UnicodeUTF8));
        QTableWidgetItem *___qtablewidgetitem1 = Tableau->horizontalHeaderItem(1);
        ___qtablewidgetitem1->setText(QApplication::translate("MainWindow", "Trame", 0, QApplication::UnicodeUTF8));
        QTableWidgetItem *___qtablewidgetitem2 = Tableau->verticalHeaderItem(0);
        ___qtablewidgetitem2->setText(QApplication::translate("MainWindow", "Commande", 0, QApplication::UnicodeUTF8));
        QTableWidgetItem *___qtablewidgetitem3 = Tableau->verticalHeaderItem(1);
        ___qtablewidgetitem3->setText(QApplication::translate("MainWindow", "R\303\251ponse", 0, QApplication::UnicodeUTF8));

        const bool __sortingEnabled = Tableau->isSortingEnabled();
        Tableau->setSortingEnabled(false);
        Tableau->setSortingEnabled(__sortingEnabled);

        label_9->setText(QApplication::translate("MainWindow", "Trame de l'\303\251change :", 0, QApplication::UnicodeUTF8));
        MessageBluetooth->setText(QApplication::translate("MainWindow", "Trame", 0, QApplication::UnicodeUTF8));
        CouleurGraph->setText(QApplication::translate("MainWindow", "COULEUR", 0, QApplication::UnicodeUTF8));
        tabWidget->setTabText(tabWidget->indexOf(tab_graph), QApplication::translate("MainWindow", "graphique", 0, QApplication::UnicodeUTF8));
        menuFichier->setTitle(QApplication::translate("MainWindow", "Fichier", 0, QApplication::UnicodeUTF8));
        menuEdition->setTitle(QApplication::translate("MainWindow", "Edition", 0, QApplication::UnicodeUTF8));
        menuAide->setTitle(QApplication::translate("MainWindow", "Aide", 0, QApplication::UnicodeUTF8));
        toolBar->setWindowTitle(QApplication::translate("MainWindow", "toolBar", 0, QApplication::UnicodeUTF8));
    } // retranslateUi

};

namespace Ui {
    class MainWindow: public Ui_MainWindow {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_MAINWINDOW_H
