import "vite/modulepreload-polyfill";
import {Application} from "@hotwired/stimulus";
import {registerControllers} from "stimulus-vite-helpers";

// @ts-ignore
import * as Turbo from "@hotwired/turbo";

const application = Application.start();
const controllers = import.meta.glob("./**/*_controller.ts", {eager: true});
console.log(controllers)
registerControllers(application, controllers);