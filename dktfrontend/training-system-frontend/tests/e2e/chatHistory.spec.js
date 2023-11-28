const {By, Builder, Browser, Key, until} = require('selenium-webdriver');
const {suite} = require('selenium-webdriver/testing');
const assert = require("assert");
require("chromedriver");

suite(function (env) {
    describe('chat history', function () {
        let driver;

        before(async function () {
            driver = await new Builder().forBrowser('chrome').build();
            await driver.manage().setTimeouts({implicit: 2000});
        });

        after(async () => {
            await driver.quit();
        });

        it('signup', async () => {
            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Sign Up')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/signup'));

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('benTen');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('benTen@gmail.com');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));
        });

        it('start a chat', async () => {
            let textBox = await driver.findElement(By.id('text-box'));
            await textBox.sendKeys('hi');

            const textBoxValue = await textBox.getAttribute('value');
            assert.equal(textBoxValue, "hi");

            await textBox.sendKeys(Key.ENTER);

            let clientMessage = await driver.wait(until.elementLocated(By.className('msg from')));
            const clientMessageValue = await clientMessage.getText();
            assert.equal(clientMessageValue, "hi");

            let liloBotMessage = await driver.wait(until.elementLocated(By.className('msg to')));
            await driver.wait(until.elementIsVisible(liloBotMessage));
        });

        it('go to history', async () => {
            await driver.findElement(By.xpath("//*[contains(text(), 'benTen')]")).click();

            await driver.wait(until.elementLocated(By.className('submenu')));

            await driver.findElement(By.xpath("//*[contains(text(), 'Chat History')]")).click();

            await driver.wait(until.urlIs('http://localhost:5601/history'));

            let historyEntries = await driver.wait(until.elementsLocated(By.className('chat-label')));

            assert.equal(historyEntries.length >= 1, true);
        });
    });
}, { browsers: [Browser.CHROME]});