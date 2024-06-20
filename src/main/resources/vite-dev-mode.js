const vitePort = 5173;
const viteClientUrl = `http://localhost:${vitePort}/@vite/client`;
const viteMainUrl = `http://localhost:${vitePort}/src/main/typescript/main.ts`;
const checkInterval = 5000;
let initialCheckFailed = false;

async function checkViteServer() {
    try {
        const response = await fetch(viteClientUrl, {method: "GET"});
        if (response.ok) {
            hideWarningDialog();

            const scriptClient = document.createElement("script");
            scriptClient.type = "module";
            scriptClient.src = viteClientUrl;
            document.body.appendChild(scriptClient);

            const scriptMain = document.createElement("script");
            scriptMain.type = "module";
            scriptMain.src = viteMainUrl;
            document.body.appendChild(scriptMain);
        } else {
            throw new Error("Vite server not running");
        }
    } catch (error) {
        console.log(error);
        if (initialCheckFailed === false) {
            initialCheckFailed = true;
            showWarningDialog();
        }
        console.warn("Vite server is not running at port", vitePort);
        setTimeout(checkViteServer, checkInterval);
    }
}

function showWarningDialog() {
    const dialog = appendToDom(`
          <dialog id="viteWarningDialog">
            <p>Warning: Vite server is not running at port 5173.</p>
            <p>Run <code>npm run dev</code> to lauch Vite.</p>
          
            <form method="dialog">
              <button>OK</button>
            </form>
          </dialog>
        `);
    dialog.showModal();
}

function appendToDom(html) {
    const placeholder = document.createElement("div");
    placeholder.innerHTML = html;
    const element = placeholder.firstElementChild;
    document.body.appendChild(element);
    return element;
}

function hideWarningDialog() {
    const dialog = document.getElementById("viteWarningDialog");
    if (dialog) {
        dialog.close();
    }
}

await checkViteServer();