const {By, Builder, Browser, Key, until} = require('selenium-webdriver');
const {suite} = require('selenium-webdriver/testing');
const assert = require("assert");
require("chromedriver");

suite(function (env) {
    describe('WebChat', function () {
        let driver;

        before(async function () {
            driver = await new Builder().forBrowser('chrome').build();
            await driver.manage().setTimeouts({implicit: 2000});
            await driver.get('http://localhost:5601');
        });

        after(async () => {
            await driver.quit();
        });

        it('send message results in message from lilobot', async () => {
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

        it('chat box wipes on exit', async () => {
            let textBox = await driver.findElement(By.id('text-box'));
            await textBox.sendKeys('hi');

            const textBoxValue = await textBox.getAttribute('value');
            assert.equal(textBoxValue, "hi");

            await textBox.sendKeys(Key.ENTER);

            let clientMessage = await driver.wait(until.elementLocated(By.className('msg from')));
            const clientMessageValue = await clientMessage.getText();
            assert.equal(clientMessageValue, "hi");


            await driver.findElement(By.id('exit-session')).click();

            let messages = await driver.findElements(By.className('msg from'));
            assert.equal(messages.length, 0);
        });
    });

    describe('navbar', async () => {
        let driver;

        before(async function () {
            driver = await new Builder().forBrowser('chrome').build();
            await driver.manage().setTimeouts({implicit: 2000});
            await driver.get('http://localhost:5601');
        });

        after(async () => {
            await driver.quit()
        });

        it('training portal', async () => {
            let header = await driver.findElements(By.xpath("//*[contains(text(), 'Training Portal')]"));
            assert.equal(header.length, 0)
        });

        it('chat with lilobot', async () => {
            await driver.findElement(By.xpath("//*[contains(text(), 'Chat with Lilobot')]")).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));
        });

        it('sign up', async () => {
            await driver.findElement(By.xpath("//*[contains(text(), 'Sign Up')]")).click();

            await driver.wait(until.urlIs('http://localhost:5601/signup'));
        });

        it('log in', async () => {
            await driver.findElement(By.xpath("//*[contains(text(), 'Log In')]")).click();

            await driver.wait(until.urlIs('http://localhost:5601/login'));
        });
    });
}, { browsers: [Browser.CHROME]});