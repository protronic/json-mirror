package software.protronic.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

class Mapper {

  // public Map<Integer, List<String>> readJExcel(String fileLocation) throws IOException {

  //   Map<Integer, List<String>> data = new HashMap<>();

  //   Workbook workbook = Workbook.getWorkbook(new File(fileLocation));
  //   Sheet sheet = workbook.getSheet("");
  //   int rows = sheet.getRows();
  //   int columns = sheet.getColumns();

  //   for (int i = 0; i < rows; i++) {
  //     data.put(i, new ArrayList<String>());
  //     for (int j = 0; j < columns; j++) {
  //       data.get(i).add(sheet.getCell(j, i).getContents());
  //     }
  //   }
  //   return data;
  // }



}
