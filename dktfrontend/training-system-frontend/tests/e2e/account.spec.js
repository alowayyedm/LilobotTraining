const {By, Builder, Browser, Key, until} = require('selenium-webdriver');
const {suite} = require('selenium-webdriver/testing');
const assert = require("assert");
require("chromedriver");

suite(function (env) {
    describe('create account', function () {
        let driver;

        before(async function () {
            driver = await new Builder().forBrowser('chrome').build();
            await driver.manage().setTimeouts({implicit: 2000});
        });

        after(async () => {
            await driver.quit();
        });

        it('signup valid', async () => {
            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Sign Up')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/signup'));

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('ben0');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('ben0@gmail.com');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));

            await driver.findElement(By.xpath("//*[contains(text(), 'ben0')]")).click();
        });

        it('signup invalid email', async () => {
            await driver.get('http://localhost:5601/signup');

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('ben2');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('ben@gmai');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            let errors = await driver.findElements(By.className('error-list'));
            assert.equal(errors.length, 1);
            await driver.findElement(By.xpath("//*[contains(text(), 'Invalid email.')]")).click();
        });

        it('signup invalid code', async () => {
            await driver.get('http://localhost:5601/signup');

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('benX');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('benX@gmail.com');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('invalid');

            await driver.findElement(By.id('login')).click();

            let errors = await driver.findElements(By.className('error-list'));
            assert.equal(errors.length, 1);
            await driver.findElement(By.xpath("//*[contains(text(), 'This code is not valid.')]")).click();
        });

        it('signup invalid password short', async () => {
            await driver.get('http://localhost:5601/signup');

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('ben3');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('ben3@gmail.com');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@123');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            let errors = await driver.findElements(By.className('error-list'));
            assert.equal(errors.length, 1);
            await driver.findElement(By.xpath("//*[contains(text(), 'Your password must be at least 8 characters long.')]")).click();
        });

        it('signup invalid username already exist', async () => {
            await driver.get('http://localhost:5601/signup');
            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('ben4');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('ben4@gmail.com');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));

            await driver.switchTo().newWindow('tab');
            await driver.get('http://localhost:5601/signup');
            let username2 = await driver.findElement(By.id('username'));
            await username2.sendKeys('ben4');

            let email2 = await driver.findElement(By.id('email'));
            await email2.sendKeys('ben5@gmail.com');

            let password2 = await driver.findElement(By.id('password'));
            await password2.sendKeys('Ben@1234');

            let code2 = await driver.findElement(By.id('code'));
            await code2.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            let errors = await driver.findElements(By.className('error-list'));
            assert.equal(errors.length, 1);
            await driver.findElement(By.xpath("//*[contains(text(), 'Username ben4 already exists')]")).click();
        });

        it('signup invalid email already exist', async () => {
            await driver.get('http://localhost:5601/signup');
            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('ben5');

            let email = await driver.findElement(By.id('email'));
            await email.sendKeys('ben5@gmail.com');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            let code = await driver.findElement(By.id('code'));
            await code.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));

            await driver.switchTo().newWindow('tab');
            await driver.get('http://localhost:5601/signup');
            let username2 = await driver.findElement(By.id('username'));
            await username2.sendKeys('ben6');

            let email2 = await driver.findElement(By.id('email'));
            await email2.sendKeys('ben5@gmail.com');

            let password2 = await driver.findElement(By.id('password'));
            await password2.sendKeys('Ben@1234');

            let code2 = await driver.findElement(By.id('code'));
            await code2.sendKeys('HyP$jdIHV$zK5#2X');

            await driver.findElement(By.id('login')).click();

            let errors = await driver.findElements(By.className('error-list'));
            assert.equal(errors.length, 1);
            await driver.findElement(By.xpath("//*[contains(text(), 'A user with the email address ben5@gmail.com already exists')]")).click();
        });
    });
    
    describe('login', function () {
        let driver;

        before(async function () {
            driver = await new Builder().forBrowser('chrome').build();
            await driver.manage().setTimeouts({implicit: 2000});
        });

        after(async () => {
            await driver.quit();
        });

        it('login invalid', async () => {
            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Log In')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/login'));

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('benDoesntExist');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            await driver.findElement(By.id('login')).click();

            let errors = await driver.wait(until.elementsLocated(By.className('error-list')));
            assert.equal(errors.length, 1);
            await driver.findElement(By.xpath("//*[contains(text(), 'Bad credentials')]"));
        })

        it('logout', async () => {
            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Log In')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/login'));

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('ben0');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));

            await driver.findElement(By.xpath("//*[contains(text(), 'ben0')]")).click();

            await driver.wait(until.elementLocated(By.className('submenu')));

            await driver.findElement(By.xpath("//*[contains(text(), 'Log Out')]")).click();

            let userTab = await driver.findElements(By.xpath("//*[contains(text(), 'ben')]"));
            assert.equal(userTab.length, 0);
        });

        it('login valid', async () => {
            await driver.get('http://localhost:5601');
            await driver.findElement(By.xpath("//*[contains(text(), 'Log In')]")).click();
            await driver.wait(until.urlIs('http://localhost:5601/login'));

            let username = await driver.findElement(By.id('username'));
            await username.sendKeys('ben0');

            let password = await driver.findElement(By.id('password'));
            await password.sendKeys('Ben@1234');

            await driver.findElement(By.id('login')).click();

            await driver.wait(until.urlIs('http://localhost:5601/'));

            await driver.findElement(By.xpath("//*[contains(text(), 'ben0')]"));
        })
    });
}, { browsers: [Browser.CHROME]});