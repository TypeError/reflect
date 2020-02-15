# Reflect

An [OWASP Zed Attack Proxy (ZAP)](https://www.zaproxy.org) add-on to help find reflected parameter vulnerabilities.

#### Features:

* Inspect in scope urls for reflected parameters
* Save requests/responses to table

![Reflect](/images/reflect.png)

## Install the Reflect add-on

### Download or build the extension
#### Option 1: Download release
You can find the latest release (ZAP file) [here](https://github.com/TypeError/reflect/releases). 

#### Option 2: Build the extension

```sh
gradle build
```

Add-on ZAP file will be located at: `./build/zapAddOn/bin`

### Load the extension
1. Open OWASP ZAP
2. File
3. Load Add-on file
4. Select reflect `.zap` file