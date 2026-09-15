package com.skyworx.ideb.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightSmokeTest {

    public static void main(String[] args) {

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
            );

            Page page = browser.newPage();

            page.navigate("http://localhost:8080/mock/ideb.html");

            System.out.println("TITLE = " + page.title());

            String nasabahName =
                    page.locator("#nasabah-name").textContent();

            String statusKredit =
                    page.locator("#status-kredit").textContent();

            String nominalTagihanText =
                    page.locator("#nominal-tagihan").textContent();

            Long nominalTagihan =
                    Long.parseLong(nominalTagihanText.trim());

            System.out.println("NASABAH NAME = " + nasabahName);
            System.out.println("STATUS KREDIT = " + statusKredit);
            System.out.println("NOMINAL TAGIHAN = " + nominalTagihan);
            System.out.println(
                    "TIPE NOMINAL = " +
                            nominalTagihan.getClass().getSimpleName()
            );

            browser.close();
        }
    }
}
