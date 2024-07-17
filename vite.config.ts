import {defineConfig} from "vite";

export default defineConfig({
    build: {
        manifest: true,
        rollupOptions: {
            input: ["src/main/typescript/main.ts"],
        },
    },
});

// import {defineConfig} from "vite";
// import path from "path";
// import fs from "node:fs";
// import {Project} from "stimulus-parser"
// export default defineConfig({
//     plugins: [cssModulesSideEffects(), writeCssModuleMappings(), writeStimulusKt()],
//     build: {
//         // minify: false,
//         manifest: true,
//         rollupOptions: {
//             input: ["src/main/typescript/main.ts"],
//         },
//     },
//     css: {
//         modules: {
//             generatedKotlinOutDir: "generated-kotlin",
//         },
//     },
// });
//
// function cssModulesSideEffects() {
//     return {
//         name: "css-modules-are-side-effects",
//         enforce: "post",
//         transform(code, id) {
//             if (id.endsWith("module.css")) {
//                 return {
//                     code,
//                     map: null,
//                     moduleSideEffects: "no-treeshake",
//                 };
//             }
//         },
//     };
// }
//
// async function writeStimulusKt() {
//     const project = new Project("src/main/typescript")
//     await project.initialize()
//     return {
//         name: "stimulus-plugin",
//         async transform(code, id) {
//             // TODO: Make relative to PWD
//             if (id.startsWith("/persistent/home/raphiz/projects/github/raphiz/kotlin-ssr-showcase/src/main/typescript")) {
//                 let relPath = path.relative("/persistent/home/raphiz/projects/github/raphiz/kotlin-ssr-showcase/", id)
//                 await project.refreshFile(relPath)
//                 let controllers = project.findProjectFile(relPath).controllerDefinitions
//                 controllers.forEach((controller) => {
//                     console.log(`object ${hyphenToPascalCase(controller.guessedIdentifier)} {`)
//                     console.log(`  object Actions {`)
//                     controller.actionNames.forEach((it) => {
//                         console.log(`    const val ${it} = \"${it}\"`)
//                     })
//                     console.log(`  }`)
//                     console.log(`  object Targets {`)
//                     controller.targets.forEach((it) => {
//                         console.log(`    const val ${it.name} = \"${it.name}\"`)
//                     })
//                     console.log(`  }`)
//                     console.log(`  object ClassNames {`)
//                     controller.classNames.forEach((it) => {
//                         console.log(`    const val ${hyphenToCamelCase(it)} = \"${it}\"`)
//                     })
//                     console.log(`  }`)
//                     console.log(`}`)
//                 });
//             }
//         }
//     }
// }
//
// function writeCssModuleMappings() {
//     let config = undefined;
//
//     let generatedKotlinOutDir = undefined;
//
//     let exportClasses = (cssFileName, json, _outputFileName) => {
//         const pathToModule = path.relative(config.root, cssFileName);
//         const generatedKotlinFile = path.resolve(
//             generatedKotlinOutDir,
//             `${pathToModule}.kt`
//         );
//         fs.mkdirSync(path.dirname(generatedKotlinFile), {recursive: true});
//
//         fs.writeFileSync(
//             generatedKotlinFile,
//             generateKotlinConstants(json, path.basename(cssFileName))
//         );
//     };
//
//     return {
//         name: "write-css-module-mappings",
//         configResolved(resolvedConfig) {
//             config = resolvedConfig;
//         },
//         config(config) {
//             if (!config.css) {
//                 config.css = {
//                     modules: {
//                         generatedKotlinOutDir: "out",
//                     },
//                 };
//             }
//             config.css.modules.getJSON = exportClasses;
//             generatedKotlinOutDir = config.css.modules.generatedKotlinOutDir;
//             if (!generatedKotlinOutDir)
//                 throw Error("Missing config key: css.modules.generatedKotlinOutDir");
//
//             fs.rmSync(generatedKotlinOutDir, {recursive: true, force: true});
//         },
//     };
// }
//
// // Function to convert file name to a valid Kotlin object name
// function toKotlinObjectName(fileName) {
//     return fileName
//         .replace(".module.css", "") // Remove the .module.css part
//         .replace(/-([a-z])/g, (g) => g[1].toUpperCase()) // Convert hyphen-case to camelCase
//         .replace(/^./, (g) => g.toUpperCase()); // Capitalize the first letter
// }
//
// function generateKotlinConstants(json, fileName) {
//     const objectName = toKotlinObjectName(fileName);
//     const constants = Object.entries(json)
//         .map(([key, value]) => `    const val ${key} = "${value}"`)
//         .join("\n");
//     return `object ${objectName} {\n${constants}\n}`;
// }
//
// function hyphenToPascalCase(str) {
//     return str
//         .split('-')
//         .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
//         .join('');
// }
//
// function hyphenToCamelCase(str) {
//     const words = str.split('-');
//     return words[0].toLowerCase() + // Keep the first word in lowercase
//         words.slice(1).map(word =>
//             word.charAt(0).toUpperCase() + word.slice(1).toLowerCase() // Capitalize the first letter of subsequent words
//         ).join('');
// }