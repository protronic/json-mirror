package software.protronic;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public enum SchemaTempEnum {
  // ERROR (-1, "{\"formular\": \"SchemaExistiertNicht\", \"felder\": []}"),
  // SERIENNUMMERVERGABE (0,
  // "{\"formular\":\"seriennummervergabe\",\"felder\":[{\"name\":\"vorgangsnummern\",\"label\":\"Vorgangsnummern:\",\"feldtyp\":\"enumtextarea\",\"beschreibung\":\"Kommagetrennte
  // Liste aller Vorgangsnummern\",\"platzhalter\":\"Bsp.: 10000, 10001, 10002,
  // ...\"},{\"name\":\"platinennnummern\",\"label\":\"Platinennummern:\",\"feldtyp\":\"enumtextarea\",\"beschreibung\":\"Kommagetrennte
  // Liste aller relevanter Platinennummern\",\"platzhalter\":\"Bsp.: 10000,
  // 10001, 10002,
  // ...\"},{\"name\":\"seriennummer\",\"label\":\"Seriennummer:\",\"feldtyp\":\"text\"},{\"name\":\"microcontrollerid\",\"label\":\"Microcontrollerid:\",\"feldtyp\":\"text\"}]}"),
  // KONTAKTFORMULAR (1,
  // "{\"formular\":\"kontaktformular\",\"felder\":[{\"name\":\"vorname\",\"label\":\"Vorname:\",\"feldtyp\":\"text\"},{\"name\":\"nachname\",\"label\":\"Nachname:\",\"feldtyp\":\"text\"},{\"name\":\"email\",\"label\":\"E-Mail:\",\"feldtyp\":\"email\",\"platzhalter\":\"beispiel@host.tld\"},{\"name\":\"anschrift\",\"label\":\"Anschrift:\",\"feldtyp\":\"object\",\"subform\":[{\"name\":\"strasse\",\"label\":\"Strasse:\",\"feldtyp\":\"text\"},{\"name\":\"hausnummer\",\"label\":\"Hausnummer:\",\"feldtyp\":\"text\"},{\"name\":\"plz\",\"label\":\"Postleitzahl:\",\"feldtyp\":\"text\"},{\"name\":\"ort\",\"label\":\"Stadt:\",\"feldtyp\":\"text\"}]},{\"name\":\"telefonnummer\",\"label\":\"Telefonnummer:\",\"feldtyp\":\"tel\"},{\"name\":\"gruppe\",\"label\":\"Gruppe:\",\"feldtyp\":\"dropdown\",\"items\":[\"Gruppe
  // 1\",\"Gruppe 2\",\"Gruppe 3\",\"Gruppe 4\",\"Gruppe 5\",\"Gruppe 6\"]}]}");

  ERROR(-1), SERIENNUMMERVERGABE(0), KONTAKTFORMULAR(1);

  private int schemaId;

  SchemaTempEnum(int id) {
    this.schemaId = id;
  }

  // SchemaTempEnum(int id, String content) {
  // this.schemaId = id;
  // this.schemaContent = content;
  // }

  public static SchemaTempEnum getSchemaById(int id) {
    try {
      return Arrays.stream(SchemaTempEnum.values()).filter(schema -> (schema.schemaId == id))
          .collect(Collectors.toList()).get(0);
    } catch (IndexOutOfBoundsException exception) {
      return ERROR;
    }
  }

  public static SchemaTempEnum getIdByName(String name) {
    try {
      System.out.println();
      return Arrays.stream(SchemaTempEnum.values())
          .filter(schema -> name.toLowerCase().contains(schema.name().toLowerCase())).collect(Collectors.toList())
          .get(0);
    } catch (IndexOutOfBoundsException exception) {
      return ERROR;
    }
  }

  /**
   * @return the schemaId
   */
  public int getSchemaId() {
    return schemaId;
  }
}
