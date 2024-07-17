package io.github.raphiz.ssr.support

import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThrows
import strikt.assertions.containsExactly
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEqualTo

class AssetLocatorTest {
    private val assetLocator = AssetLocator.fromManifest(MANIFEST)

    @Test
    fun `finds script for entry point`() {
        val fooScript = assetLocator.locate("views/foo.js").scripts
        val barScript = assetLocator.locate("views/bar.js").scripts
        expect {
            that(fooScript).containsExactly("assets/foo-BRBmoGS9.js")
            that(barScript).containsExactly("assets/bar-gkvgaI9m.js")
        }
    }

    @Test
    fun `finds module preloads for entry point`() {
        val fooScript = assetLocator.locate("views/foo.js").preloads
        val barScript = assetLocator.locate("views/bar.js").preloads
        expect {
            that(fooScript).containsExactlyInAnyOrder("assets/shared-B7PI925R.js")
            that(barScript).containsExactlyInAnyOrder("assets/shared-B7PI925R.js")
        }
    }

    @Test
    fun `finds css for entry point`() {
        val fooScript = assetLocator.locate("views/foo.js").css
        val barScript = assetLocator.locate("views/bar.js").css

        expect {
            that(fooScript).containsExactlyInAnyOrder("assets/foo-5UjPuW-k.css", "assets/shared-ChJ_j-JJ.css")
            that(barScript).containsExactly("assets/shared-ChJ_j-JJ.css")
        }
    }

    @Test
    fun `throws when entry point does not exist`() {
        expectThrows<IllegalStateException> {
            assetLocator.locate("views/noop.js")
        }.get { message }.isEqualTo("views/noop.js is not an entrypoint or is missing in the manifest")
    }

    @Test
    fun `throws when file is not an entry point`() {
        expectThrows<IllegalStateException> {
            assetLocator.locate("baz.js")
        }.get { message }.isEqualTo("baz.js is not an entrypoint or is missing in the manifest")
    }
}

private const val MANIFEST = """
{
  "_shared-PUGYiKTe.js": {
    "file": "assets/shared-ChJ_j-JJ.css",
    "src": "_shared-PUGYiKTe.js"
  },
  "_shared-B7PI925R.js": {
    "file": "assets/shared-B7PI925R.js",
    "name": "shared",
    "css": ["assets/shared-ChJ_j-JJ.css"]
  },
  "baz.js": {
    "file": "assets/baz-B2H3sXNv.js",
    "name": "baz",
    "src": "baz.js",
    "isDynamicEntry": true
  },
  "views/bar.js": {
    "file": "assets/bar-gkvgaI9m.js",
    "name": "bar",
    "src": "views/bar.js",
    "isEntry": true,
    "imports": ["_shared-B7PI925R.js"],
    "dynamicImports": ["baz.js"]
  },
  "views/foo.js": {
    "file": "assets/foo-BRBmoGS9.js",
    "name": "foo",
    "src": "views/foo.js",
    "isEntry": true,
    "imports": ["_shared-B7PI925R.js"],
    "css": ["assets/foo-5UjPuW-k.css"]
  }
}
"""
