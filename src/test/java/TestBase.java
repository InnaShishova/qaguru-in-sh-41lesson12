import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.remote.DesiredCapabilities;
import utils.Attach;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.logevents.SelenideLogger.addListener;

public class TestBase {

    @BeforeAll
    static void beforeAll() {
        String baseUrl = System.getProperty(
                "baseUrl",
                "https://demoqa.com"
        );

        String remoteUrl = System.getProperty(
                "remoteUrl",
                ""
        );

        String browser = System.getProperty(
                "browser",
                "chrome"
        );

        String browserVersion = System.getProperty(
                "browserVersion",
                ""
        );

        String browserSize = System.getProperty(
                "browserSize",
                "1920x1080"
        );

        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", "false")
        );

        Configuration.baseUrl = baseUrl;
        Configuration.browser = browser;
        Configuration.browserSize = browserSize;
        Configuration.headless = headless;
        Configuration.pageLoadStrategy = "eager";
        Configuration.timeout = 10000;

        if (!browserVersion.isBlank()) {
            Configuration.browserVersion = browserVersion;
        }

        if (!remoteUrl.isBlank()) {
            Configuration.remote = remoteUrl;

            DesiredCapabilities capabilities = new DesiredCapabilities();

            capabilities.setCapability(
                    "selenoid:options",
                    Map.of(
                            "enableVNC", true,
                            "enableVideo", true
                    )
            );

            Configuration.browserCapabilities = capabilities;
        }

        addListener(
                "AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
        );
    }

    @AfterEach
    void afterEach() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Attach.screenshotAs("Last screenshot");
            Attach.pageSource();
            Attach.browserConsoleLogs();

            if (Configuration.remote != null
                    && !Configuration.remote.isBlank()) {
                Attach.addVideo();
            }

            closeWebDriver();
        }
    }
}