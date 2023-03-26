package org.example.utils;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.PersonalInfoFormTO;
import org.example.model.VisaFormTO;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IoUtils {

    private final static Logger logger = LogManager.getLogger(IoUtils.class);
    private final static String S3_BUCKET_NAME = "auslander-termin-files";
    private static AmazonS3 client;
    public static boolean isS3Enabled = false;

    public static PersonalInfoFormTO readPersonalInfoFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = PersonalInfoFormTO.class.getResourceAsStream("/DEFAULT_PERSONAL_INFO_FORM.json");
        try {
            return mapper.readValue(is, PersonalInfoFormTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static VisaFormTO readVisaInfoFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = VisaFormTO.class.getResourceAsStream("/DEFAULT_VISA_APPLICATION_FORM.json");
        try {
            return mapper.readValue(is, VisaFormTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void savePage(WebDriver driver, String pageDescriber, String suffix) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dateAsStr = dtf.format(now);
        String fileName = pageDescriber + "_" + dateAsStr + "_" + suffix;
        String pagesourceFileName = fileName + ".html";
        String screenshotFileName = fileName + ".png";
        logger.info("File name :{}, {}", pagesourceFileName, screenshotFileName);

        String content = driver.getPageSource();
        File sourceFile;
        try {
            sourceFile = saveSourceCodeToFile(content, pagesourceFileName);
        } catch (IOException e) {
            logger.error("Error occurred during IO operation. Exception: ", e);
            return;
        }
        File screenShotFile;
        try {
            screenShotFile = saveScreenshot(driver, screenshotFileName);
        } catch (IOException e) {
            logger.error("Error occurred during IO operation. Exception: ", e);
            return;
        }

        if (isS3Enabled) {
            client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new EnvironmentVariableCredentialsProvider())
                    .withRegion(Regions.US_EAST_1)
                    .build();

            try {
                client.putObject(new PutObjectRequest(S3_BUCKET_NAME, fileName, sourceFile));
                client.putObject(new PutObjectRequest(S3_BUCKET_NAME, fileName, screenShotFile));
            } catch (Exception e) {
                logger.error("Error occurred during s3 operation. Exception: ", e);
            }

        }
    }

    private static File saveSourceCodeToFile(String content, String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fw;
        fw = new FileWriter(file);
        fw.write(content);
        fw.close();
        return file;
    }

    private static File saveScreenshot(WebDriver driver, String fileName) throws IOException {
        File scrFile1 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File file = null;

        file = new File(fileName);
        FileUtils.copyFile(scrFile1, file);

        return file;
    }

}