package com.infodation.user.MongoDbOAuth.models;

import com.infodation.user.MongoDbOAuth.utils.ConvertValueUtil;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class CSVDataMiner extends DataMiner {

    @Override
    protected void openFile(MultipartFile file) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(bufferedReader)) {
            List<String[]> rows = csvReader.readAll();
            readKey(rows.get(0), rows.get(1));

            for (int i = 1; i < rows.size(); i++) {
                readRow(rows.get(i));
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    protected void readKey(String[] head, String[] firstRow) {
        setKeys(new String[2][head.length]);
        try {
            for (int i = 0; i < head.length ; i++) {
                this.getKeys()[0][i] = head[i];
                this.getKeys()[1][i] = ConvertValueUtil.convertWithKey(head[i], firstRow[i]);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

}
