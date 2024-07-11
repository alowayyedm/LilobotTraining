const {By, Builder, Browser, Key, until} = require('selenium-webdriver');
const {suite} = require('selenium-webdriver/testing');
const assert = require("assert");
require("chromedriver");

suite(function (env) {
    describe('Trainer join learner session workflow', function () {
        let driver;
        let trainerTab;
        let learnerTab;


        before(async function () {
            driver = await new Builder().forBrowser('chrome').build();
        });

        after(async () => {
            await driver.quit();
        });



        it('signup trainer account', async () => {
            trainerTab = await driver.getWindowHandle();

            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Sign Up')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/signup'));

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('trainer');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('trainer@gmail.com');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            await driver.findElement(By.id('trainer')).click();

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));
        });

        it('signup learner account', async () => {
            await driver.switchTo().newWindow('tab');
            learnerTab = await driver.getWindowHandle();

            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Sign Up')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/signup'));

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('ben');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('learner@gmail.com');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));
        });

        it('learner assigns trainer', async () => {
            await driver.findElement(By.xpath("//*[contains(text(), 'ben')]")).click();

            await driver.wait(until.elementLocated(By.className('submenu')));

            await driver.findElement(By.xpath("//*[contains(text(), 'Settings')]")).click();

            await driver.wait(until.urlIs('http://localhost:5601/settings'));

            await driver.wait(until.elementLocated(By.id('trainers-button'))).click();

            let trainerTextBox = await driver.wait(until.elementLocated(By.name('trainer-username')));
            trainerTextBox.sendKeys('trainer');

            await driver.wait(until.elementLocated(By.className('add-button'))).click();

            await driver.wait(until.elementLocated(By.xpath("//*[contains(text(), 'Chat with Lilobot')]"))).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));
        });

        it('learner start chat', async () => {
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

        it('trainer try to join learner session', async () => {
            await driver.switchTo().window(trainerTab);
            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Training Portal')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/train'));

            await driver.findElement(By.name("request-session")).click();

            let usernameTextBox = await driver.wait(until.elementLocated(By.id("username")));
            await usernameTextBox.sendKeys('ben');
            await driver.findElement(By.name("join-session")).click();
        });

        it('learner accept session', async () => {
            await driver.switchTo().window(learnerTab);
            await driver.wait(until.elementLocated(By.id("accept"))).click();
        });

        it('trainer should have messages in chat', async () => {
            await driver.switchTo().window(trainerTab);

            let messages = await driver.wait(until.elementsLocated(By.xpath("(//span[@class='message'])[2]")));

            assert.equal(messages.length, 1);
        });

        it('trainer sending message to learner', async () => {
            let messages = await driver.wait(until.elementsLocated(By.className("msg")));
            assert.equal(messages.length > 1, true);

            let checkbox = await driver.findElement(By.className('slider'));
            await driver.executeScript("arguments[0].scrollIntoView(true);", checkbox)
            await checkbox.click();

            let textBox = await driver.wait(until.elementLocated(By.id('text-box')));
            await textBox.sendKeys('hello');

            const textBoxValue = await textBox.getAttribute('value');
            assert.equal(textBoxValue, "hello");

            await textBox.sendKeys(Key.ENTER);


            let trainerMessage = await driver.wait(until.elementLocated(By.xpath("(//span[@class='message'])[3]")));
            const trainerMessageValue = await trainerMessage.getText();
            assert.equal(trainerMessageValue, "hello");
        });

        it('learner can see hello', async () => {
            await driver.switchTo().window(learnerTab);

            let trainerMessage = await driver.wait(until.elementLocated(By.xpath("(//span[@class='message'])[3]")));
            const trainerMessageValue = await trainerMessage.getText();
            assert.equal(trainerMessageValue, "hello");
        });

        it('trainer modifies message and sends', async () => {
            await driver.switchTo().window(trainerTab);

            let pendingMessage = await driver.wait(until.elementLocated(By.xpath("(//button[@class='option-button edit'])[1]")));
            await pendingMessage.click();

            await driver.wait(until.elementLocated(By.className('editing')))

            await driver.findElement(By.id('text-box')).sendKeys("test");
            await driver.findElement(By.id('text-box')).sendKeys(Key.ENTER);
        });

        it('learner sees modified message', async () => {
            await driver.switchTo().window(learnerTab);

            let trainerMessage = await driver.wait(until.elementLocated(By.xpath("(//span[@class='message'])[4]")));
            const trainerMessageValue = await trainerMessage.getText();
            assert.equal(trainerMessageValue.toString().endsWith("test"), true);
        });

        it('trainer uses features', async () => {
            await driver.switchTo().window(trainerTab);

            await driver.wait(until.elementLocated(By.xpath("(//button[@title='(Re-)generate optimal path'])[1]"))).click();

            await driver.wait(until.elementLocated(By.className('message-node')))
        });
    });
}, { browsers: [Browser.CHROME]});