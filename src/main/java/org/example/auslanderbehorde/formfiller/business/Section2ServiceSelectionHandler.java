package org.example.auslanderbehorde.formfiller.business;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.auslanderbehorde.formfiller.enums.SeleniumProcessEnum;
import org.example.auslanderbehorde.formfiller.enums.SeleniumProcessResultEnum;
import org.example.auslanderbehorde.formfiller.exceptions.ElementNotFoundTimeoutException;
import org.example.auslanderbehorde.formfiller.exceptions.InteractionFailedException;
import org.example.auslanderbehorde.formfiller.model.PersonalInfoFormTO;
import org.example.auslanderbehorde.formfiller.model.VisaFormTO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.stream.Collectors;

import static org.example.auslanderbehorde.formfiller.business.FormFillerUtils.*;
import static org.example.auslanderbehorde.formfiller.business.Section3DateSelectionHandler.foundAppointmentCount;
import static org.example.auslanderbehorde.formfiller.business.Section3DateSelectionHandler.handledAppointmentCount;
import static org.example.auslanderbehorde.formfiller.enums.FormParameterEnum.*;

/**
 * Business Access Layer for filling the form
 */
public class Section2ServiceSelectionHandler {

    private final Logger logger = LogManager.getLogger(Section2ServiceSelectionHandler.class);
    private final String citizenshipValue;
    private final String applicantNumber;
    private final String familyStatus;
    private final String serviceTypeLabelValue;
    private final String visaLabelValue;
    private final String visaPurposeLabelValue;

    private static int searchCount = 0;

    private RemoteWebDriver driver;

    public Section2ServiceSelectionHandler(VisaFormTO visaFormTO, PersonalInfoFormTO personalInfoFormTO, RemoteWebDriver remoteWebDriver) {
        this.visaPurposeLabelValue = visaFormTO.getVisaPurposeValue();
        this.driver = remoteWebDriver;
        this.citizenshipValue = personalInfoFormTO.getCitizenshipValue();
        this.applicantNumber = personalInfoFormTO.getApplicationsNumber();
        this.familyStatus = personalInfoFormTO.getFamilyStatus();
        this.serviceTypeLabelValue = visaFormTO.getServiceType();
        this.visaLabelValue = visaFormTO.getVisaLabelValue();
    }

    public void fillAndSendForm() throws ElementNotFoundTimeoutException, InteractionFailedException, InterruptedException {
        logger.info("Starting to fill the form");
        selectCitizenshipValue();
        selectApplicantsCount();
        selectFamilyStatus();
        clickServiceType();
        clickVisaPurpose();
        clickToVisa();
        Thread.sleep(2000);
        sendForm();
    }

    private void selectCitizenshipValue() throws InterruptedException, ElementNotFoundTimeoutException {
        String elementDescription = COUNTRY.name();
        int i = 1;
        while (i <= TIMEOUT_FOR_GETTING_ELEMENT_IN_SECONDS) {
            try {
                WebElement element = driver.findElements(By.tagName("select")).stream().filter(element1 -> element1.getAttribute("name").equals("sel_staat")).collect(Collectors.toList()).get(0);
                Select select = new Select(element);
                select.selectByVisibleText(citizenshipValue);
                WebElement option = select.getFirstSelectedOption();
                String selectValue = option.getText();
                logInfo(elementDescription, SeleniumProcessEnum.SELECTING_OPTION, "Successful", "value" + selectValue);
                logInfo(elementDescription, SeleniumProcessEnum.GETTING_BY_ID, SeleniumProcessResultEnum.SUCCESSFUL.name());
                Thread.sleep(SLEEP_DURATION_IN_MILLISECONDS);
                break;
            } catch (Exception e) {
                //logWarn(elementDescription, SeleniumProcessEnum.GETTING_BY_ID.firstName(), SeleniumProcessResultEnum.FAILED.firstName(), "");
            }
            Thread.sleep(SLEEP_DURATION_IN_MILLISECONDS);
            i++;
        }
        if (i > TIMEOUT_FOR_GETTING_ELEMENT_IN_SECONDS) {
            logWarn(elementDescription, SeleniumProcessEnum.GETTING_BY_ID.name(), SeleniumProcessResultEnum.FAILED.name(), "");
            throw new ElementNotFoundTimeoutException(elementDescription);
        }
    }

    private void selectApplicantsCount() throws InterruptedException, ElementNotFoundTimeoutException {
        String elementDescription = APPLICANT_COUNT.name();
        int i = 1;
        while (i <= TIMEOUT_FOR_GETTING_ELEMENT_IN_SECONDS) {
            try {
                WebElement element = driver.findElements(By.tagName("select")).stream().filter(element1 -> element1.getAttribute("name").equals("personenAnzahl_normal")).collect(Collectors.toList()).get(0);
                Select select = new Select(element);
                select.selectByValue(applicantNumber);
                WebElement option = select.getFirstSelectedOption();
                String selectValue = option.getText();
                logInfo(elementDescription, SeleniumProcessEnum.SELECTING_OPTION, "Successful", "value" + selectValue);
                logInfo(elementDescription, SeleniumProcessEnum.GETTING_BY_ID, SeleniumProcessResultEnum.SUCCESSFUL.name());
                Thread.sleep(SLEEP_DURATION_IN_MILLISECONDS);
                break;
            } catch (Exception e) {
                //logWarn(elementDescription, SeleniumProcessEnum.GETTING_BY_ID.firstName(), SeleniumProcessResultEnum.FAILED.firstName(), "");
            }
            Thread.sleep(SLEEP_DURATION_IN_MILLISECONDS);
            i++;
        }
        if (i > TIMEOUT_FOR_GETTING_ELEMENT_IN_SECONDS) {
            logWarn(elementDescription, SeleniumProcessEnum.GETTING_BY_ID.name(), SeleniumProcessResultEnum.FAILED.name(), "");
            throw new ElementNotFoundTimeoutException(elementDescription);
        }
    }

    private void selectFamilyStatus() throws InterruptedException, ElementNotFoundTimeoutException {
        int i = 1;
        String elementDescription = familyStatus;
        while (i <= TIMEOUT_FOR_GETTING_ELEMENT_IN_SECONDS) {
            try {
                WebElement element = driver.findElements(By.tagName("select")).stream().filter(element1 -> element1.getAttribute("name").equals("lebnBrMitFmly")).collect(Collectors.toList()).get(0);
                Select select = new Select(element);
                select.selectByValue(familyStatus);
                logInfo(elementDescription, SeleniumProcessEnum.GETTING_BY_ID, SeleniumProcessResultEnum.SUCCESSFUL.name());
                Thread.sleep(SLEEP_DURATION_IN_MILLISECONDS);
                break;
            } catch (Exception e) {
                //logWarn(elementDescription, SeleniumProcessEnum.GETTING_BY_ID.firstName(), SeleniumProcessResultEnum.FAILED.firstName(), "");
            }
            Thread.sleep(SLEEP_DURATION_IN_MILLISECONDS);
            i++;
        }
        if (i > TIMEOUT_FOR_GETTING_ELEMENT_IN_SECONDS) {
            logWarn(elementDescription, SeleniumProcessEnum.GETTING_BY_ID.name(), SeleniumProcessResultEnum.FAILED.name(), "");
            throw new ElementNotFoundTimeoutException(elementDescription);
        }
    }

    private void clickServiceType() throws InterruptedException, ElementNotFoundTimeoutException, InteractionFailedException {
        WebElement element = FormFillerUtils.getElementByLabelValue(serviceTypeLabelValue, serviceTypeLabelValue, driver);
        FormFillerUtils.clickToElement(element, serviceTypeLabelValue);
    }

    private void clickVisaPurpose() throws InterruptedException, ElementNotFoundTimeoutException, InteractionFailedException {
        WebElement element = FormFillerUtils.getElementByLabelValue(visaPurposeLabelValue, visaPurposeLabelValue, driver);
        FormFillerUtils.clickToElement(element, visaPurposeLabelValue);
    }

    private void clickToVisa() throws InterruptedException, ElementNotFoundTimeoutException, InteractionFailedException {
        WebElement element = FormFillerUtils.getElementByLabelValue(visaLabelValue, visaLabelValue, driver);
        FormFillerUtils.clickToElement(element, visaLabelValue);
    }

    private void sendForm() throws InterruptedException, ElementNotFoundTimeoutException, InteractionFailedException {
        String elementXpath = "//*[@id=\"applicationForm:managedForm:proceed\"]";
        String elementDescription = "clickButton".toUpperCase();
        WebElement element = FormFillerUtils.getElementByXPath(elementXpath, elementDescription, driver);
        FormFillerUtils.clickToElement(element, elementDescription);
        searchCount++;
        String msg = String.format("SuccessfullyFormSenCount:%s, HandledAppoi.Count:%s, Found count: %s", searchCount, handledAppointmentCount, foundAppointmentCount);
        logger.info(msg);
    }

    public RemoteWebDriver getDriver() {
        return this.driver;
    }
}
