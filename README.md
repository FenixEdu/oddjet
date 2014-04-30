# ODDJET

Open Document Driven Java Empowered Templating

## Description

## Usage Instructions

### The Template Class

### User Fields for Single Data

User fields allow inserting a single variable data string in a template. Through the user field this string may be used within the text or in conditions within conditional content structures.

In the process of instantiating a template the names of the user fields within the template document are matched against the parameters supplied to the template class object. In case a match is found for a field's name, the matching object's string representation for the template's locale is obtained and set as the its value.

#### Parameter Name Matching

The matching process is not restricted to a simple exact name match with a provided parameter, it also supports access to the public contents of any parameter object to any depth. This is done by separating the desired attribute's names with a dot "." forming what will be from now on called an attribute chain. Each name in the chain results in an object, by getting and evaluating (in this order):
* a key's value if in a <code>Map\<String,?\></code> object
* a public field of the object
* a public method of the object with 0 parameters having that exact name or having it preceded by "get","is" or "has"
The first name will be searched within the <code>Template</code> class's parameter <code>Map</code>.
 
In case of failing to resolve the chain an error is printed and the field is not altered, allowing the value assigned in the template to be used as a default value. Failures may result from:
* The matching attribute or method not being accessible
* An intermediate object in the chain being null
* Matching method requiring arguments 
* Internal exceptions occurring during the matching method's execution.
* Not finding an appropriate key, field or method that matches the given name

#### Obtaining the String Representation

The matched object's string representation is usually obtained via its toString() method. However, to deal with localized data, the process begins by searching the object for a method matching getContent(Locale). If this method exists it is evaluated with the Template object's locale and the toString() method applied to the result instead. If an <code>Exception</code> occurs while attempting to call the method then the original object is used as if the method didn't exist. In either case, if the object is null an empty string is returned.

#### Working Examples

A field

    User Field person.father.spouse.nationality.foreign = 
could correspond to the object obtained evaluating:

    Template.getParameters().get("person").getFather().getSpouse().getNationality().isForeign()
Resulting in setting the field value to "true" or "false".
___

A field

    User Field image_background.color = N/A
could correspond to the object obtained evaluating:

    Template.getParameters().get("image_background").color()
Resulting in setting the field value to a localized string representation of a color such as "Red","Rojo","Rød", "Rouge" or "Vermelho".
Also, in this example, if there was a failure resolving the attribute chain the field value would be kept, providing "N/A" as an acceptable default value.

#### Notes, Caveats and Recommendations
* To obtain a method the first letter in the attribute's name does not need to be in upper case, this is done automatically.
* The attribute chain notation prohibits the names of template parameters to contain dots.
* Failure in resolving an attribute chain is not equal to obtaining null as the final result. In that case, the field's value is set to null's string representation - the empty string.
* Some care may be needed with the order in which an attribute is searched within the object since our conflict resolution is based on it. For example, if a class implements <code>Map\<String,?\></code> and has a key with the same name as one of its attributes then the matching process will always retrieve the key's value and not the attribute's.
* The chain mechanism allows to call any 0-argument method whether they return an object or not. In the latter case the result will just be null. No type of side-effect contention is performed so care is recommended to avoid data inconsistencies due to those. While doing this intentionally may be useful in some cases, it is generally spurned as it violates the separation of responsibilities by having the template instantiation process alter data.

### Tables for Data Collections

#### Table Syntax

### Conditional Content with Hidden/Conditional Text, Hidden Paragraphs and Hidden Sections

### Instantiating, Saving and Printing

#### Openoffice Headless Process and Connection

##### Script

### Template document problems

#### Script fixes

## LibreOffice Writer Tips and How To's

This section gives some tips and explains how to use and create the ODT format structures that are most important for our API using LibreOffice Writer. Although the interface's differ, most tips and explanations are valid for other word processors such as OpenOffice Writer too.

### How to Create Fields

* Access the Fields window via the menu Insert > Fields > Other... or by hitting Ctrl-F2.
* Pick the type of field by choosing the tab and corresponding type within the tab.
* Choose/fill the field's parameters.
* Place the cursor in the location where you want to insert the field.
* Click Insert or hit Alt-i with the Fields window selected.

#### Notably Useful Fields

* Variables > User Field - To use single data variables within the text or in conditions.
* Functions > Hidden Text/Conditional Text/Hidden Paragraph - To develop conditional content.
* Document  > Date/Page/Statistics/Time - To avoid passing these types of data through parameters without need.

#### Creating User Fields

Creating user fields is easy using the method above, however since these are used for our API some cares are necessary:
* It is recommended to set the Format parameter as "Text" or "General", other formats may lead to erroneous data representations and condition evaluations.
* If a field is to be used in conditions only it may still be useful to include it within the text in a special section of the document and check "invisible" in its parameters. This helps prevent accidental deletions and has no effect on a document print/preview. 
* If a field is to be static for some reason, make sure there is no parameter of the template object with a matching name.

#### Removing User Fields

To delete a user field deleting it in the text is not sufficient. To remove the field completely it's necessary to select it in the Fields Window and click the red X button close to the field's value. This button is only available if there are no instances of the field in the text.

Be careful when removing certain user fields since it is possible to remove those that are present only within conditions of sections or other fields. This does not result in an error, just in the field not being filled with the template parameter's data, possibly causing the involved conditions to evaluate erroneously.

#### Updating Fields

Sometimes, after the field's value has changed, the change is not reflected on the text instances of the field immediately. If necessary, field value updates can be forced by selecting in the menu Tools > Update > Fields or by hitting F9.

### How to Manage Conditional Content

#### Hidden Text

#### Hidden Paragraph

#### Hidden Sections




