#-------------------------------------------------
#
# Project created by QtCreator 2012-01-03T16:23:14
#
#-------------------------------------------------

QT       += core gui

TARGET = ProjetRobot
TEMPLATE = app

DESTDIR = "..//Poste d'analyse//Integration"

SOURCES += main.cpp\
        mainwindow.cpp \
    EchangeAscii.cpp \
    EchangeBinaire.cpp

HEADERS  += mainwindow.h \
    EchangeAscii.h \
    EchangeBinaire.h

FORMS    += mainwindow.ui
