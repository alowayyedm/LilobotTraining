export default class UserCredentials {
    constructor(username, password, email='email', role='LEARNER', code='code') {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.code = code;
    }
}