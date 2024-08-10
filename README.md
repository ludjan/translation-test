# TranslationTest

This is an attempt of creating a custom Spring annotation with supporting build scripts
to provide a simple localization experience.

## Extract strings for translation and mockTranslate them
We can scan the project tree for uses of the @ExtractForTranslation annotation 
and gather them in a generated resource file.
Then, we simulate translation with a separate gradle task which just adds the 'translation' value.
```shell
gradle mockTranslate
```

## At runtime
At runtime we scan all Spring Beans for use of the @ExtractForTranslation 
annotation and use the key to lookup the translated value in the resource file, 
or uses the default value (english).