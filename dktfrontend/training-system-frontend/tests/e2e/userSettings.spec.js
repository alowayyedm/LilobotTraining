const {By, Builder, Browser, Key, until} = require('selenium-webdriver');
const {suite} = require('selenium-webdriver/testing');
const assert = require("assert");
require("chromedriver");
const {elementsLocated} = require("selenium-webdriver/lib/until");

suite(function (env) {
    describe('change password', function () {
        let driver;


        before(async function () {
            driver = await new Builder().forBrowser('chrome').build();
        });

        after(async () => {
            await driver.quit();
        });

        it('signup', async () => {
            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Sign Up')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/signup'));

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('john');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('john@gmail.com');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('John@1234');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));
        });

        it('change password', async () => {
            await driver.get('http://localhost:5601/settings')

            await driver.wait(until.elementLocated(By.className('password-button'))).click();

            let oldPassword = await driver.wait(until.elementLocated(By.name('oldPassword')));
            await oldPassword.sendKeys('John@1234')
            await oldPassword.sendKeys(Key.ENTER);

            let newPasswordElement = await driver.wait(until.elementLocated(By.name('newPassword')));
            let newPassword = await driver.wait(until.elementIsVisible(newPasswordElement));
            await newPassword.sendKeys('Ben@1234')

            let newRepeatPasswordElement = await driver.wait(until.elementLocated(By.name('newRepeatPassword')));
            let newRepeatPassword = await driver.wait(until.elementIsVisible(newRepeatPasswordElement));
            await newRepeatPassword.sendKeys('Ben@1234')

            await driver.wait(until.elementLocated(By.className('confirm-button'))).click();
        });

        it('login with new password', async () => {
            await driver.switchTo().newWindow('tab');

            await driver.get('http://localhost:5601/login');

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('john');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));
        });
    });

    describe('change password', function () {
        let driver;


        before(async function () {
            driver = await new Builder().forBrowser('chrome').build();
        });

        after(async () => {
            await driver.quit();
        });

        it('delete account', async () => {
            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Sign Up')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/signup'));

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('doe');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('doe@gmail.com');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Doe@1234');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));
        });

        it('delete account', async () => {
            await driver.get('http://localhost:5601/settings')

            await driver.wait(until.elementLocated(By.className('delete-button'))).click();

            await driver.wait(until.elementLocated(By.id('accept'))).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));
        });

        it('login attempt fails', async () => {
            await driver.switchTo().newWindow('tab');

            await driver.get('http://localhost:5601/login');

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys("doe");

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Doe@1234');

            await driver.findElement(By.id('login')).click();

            let errors = await driver.wait(until.elementsLocated(By.className('error-list')));
            assert.equal(errors.length, 1);
            await driver.findElement(By.xpath("//*[contains(text(), 'Bad credentials')]"));
        });
    });
}, { browsers: [Browser.CHROME]});