package org.javascool.proglets.progBot;

/** D�finit la traduction d'un code Jvs en code Java  pour manipuler la proglet �progBot� (A D�TRUIRE SI NON UTILIS�).
 *
 * @see <a href="Translator.java.html">code source</a>
 * @serial exclude
 */
public class Translator extends org.javascool.core.Translator {
    @Override
     public String getImports() {
    return "";
  }
    @Override
  public String translate(String code) {
    return code;
  }
}
