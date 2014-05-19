# ODDJET

Open Document Driven Java Empowered Templating

##### Table of Contents

* [About](#about)
* [Quickstart](#quickstart)
	* [TL;DR?](#tldr)
* [Usage Instructions](#usage-instructions)
	* [The Template Class](#the-template-class)
		* [Template Data](#template-data)
		* [Instantiating, Converting to PDF and Saving](#instantiating-converting-to-pdf-and-saving)
			* [OpenOffice Headless Process and Connection](#openOffice-headless-process-and-connection)
		* [Instance Page Count](#instance-page-count)
	* [Table Data Collection Classes](#table-data-collection-classes)
	* [User Fields for Single Data Parameters](#user-fields-for-single-data-parameters)
		* [Data Parameter Name Matching](#data-parameter-name-matching)
		* [Working Examples](#working-examples)
		* [Notes, Caveats and Recommendations](#notes-caveats-and-recommendations)
	* [Tables for Data Collections](#tables-for-data-collections)
		* [Table Calls](#Table Calls)
		* [Table Configuration Parameter Types](#table-configuration-parameter-types)
			* [Header](#header)
			* [Content Structure](#content-structure)
			* [Content Direction](#content-direction)
			* [Fill Behavior](#fill-behavior)
			* [Write Behavior](#write-behavior)
			* [Style Source](#style-source)
			* [Last Border](#last-border)
		* [Automatic Table-Dependent Template Data Parameters](#automatic-table-dependent-template-data-parameters)
	* [Conditional or Hidden Fields and Sections for Conditional Content](#conditional-or-hidden-fields-and-sections-for-conditional-content)
	* [Template Document Problems](#template-document-problems)
	* [Logging](#logging)
* [LibreOffice Writer Tips and How To's](#libreoffice-writer-tips-and-how-tos)
	* [Fields](#fields)
		* [Creating](#creating)
			* [Notably Useful Field Types](#notably-useful-field-types)
		* [Deleting](#deleting)
		* [Updating](#updating)
	* [Tables](#tables)
	* [Conditional Content Structures](#conditional-content-structures)
		* [Conditional Text](#conditional-text)
		* [Hidden Text](#hidden-text)
		* [Hidden Paragraphs](#hidden-paragraphs)
		* [Hidden Sections](#hidden-sections)
* [Links of Interest](#links-of-interest)
* [Future Plans](#future-plans)


## About

ODDJET is a Java templating API that works with Open Document Text format files designed to answer the [FenixEdu](http://fenixedu.org/) project's needs in academic and administrative document templating.

By using odt format documents ODDJET intends to provide an easier and more familiar interface for template document creation. This simplifies the implementation of the standards that official documents are subject to as well as permit a more direct involvement of the departments and persons responsible for the official specifications in this process. To aid in this capacity ODDJET also promotes the separation of the logic and formatting of a document from the data insertion process as much as possible.

## Quickstart

Assuming you have a Maven Project, to start using ODDJET you just need to add the ODDJET dependency to its pom.xml:

	<dependency>
		<groupId>org.fenixedu</groupId>
        <artifactId>oddjet</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

Once this is done the project has access to the ODDJET classes and you can start using them to populate your own templates. Let's create a simple use case to use ODDJET in. Imagine that you manage a social group and want to produce for each person in the group a simple pdf document entitled "Contacts - _<name>_" with a table containing the person's contacts' name, age and email.

Start by producing your own template document, let's call it "Contacts.odt", containing a user field in the title named "person.name" (after "Contacts -") and a table below named "contacts" with three columns and 2 rows. In the top row of this table you can write the headers "Name","Age" and "Email Address" while in the bottom row you must write the names of the information categories you want in those rows "name","age" and "email". You can change the header names freely but the category, user field and table names will be linked to your java code so be careful. The sections [User Fields for Single Data Parameters](#user-fields-for-single-data-parameters) and [Tables for Data Collections](#tables-for-data-collections) can help understand the reason behind these constructs and all the options you have with them. Also, we provide an [example template file](src/examples/quickstart/templates/Contacts.odt) meeting our criteria.

Next, if you're really just imagining this use case, you'll need to build a simple domain with some persons and their contact relations. For our example's needs this domain is required only to contain a collection of objects representing persons, each with accessible attributes for their name, age, email and contacts (a list of known persons). Here "accessible" doesn't necessarily mean public, they can also be accessible via getter methods or in other ways (Check the [Data Parameter Name Matching](#data-parameter-name-matching) section for details). We have created a very [simple domain](src/example/quickstart/java/domain) for this, just covering the example's basic needs.

If you don't have one already, create the main class and function in your project. The first step in the function should be to get or create the domain.
Having the domain ready we can start using ODDJET to produce our pdf files. To do so first create a Template class object linked to your template document:

		Template template = new Template("/path/to/template/Contacts.odt");
The template object already contains the template document so, to avoid having to read that file again, let's use this same object to create the pdf instances for all of our domain's persons. For each person, your code must pass all relevant data to the template, produce the pdf file and clear all the data in the template again. Let's go one step at a time, beginning with passing the data:
	
        template.addParameter("person", targetPerson);
		template.addTableDataSource("contacts", new EntryListTableData(targetPerson.contacts));
In this code we are first adding the person we are creating the pdf for as a parameter simply named "person" and then their contacts (wrapped in one of the [Table Data Collection Classes](#table-data-collection-classes) ODDJET provides) as a table's data source named "contacts". These names might ring a bell, the parameter "person" should be used in the "person.name" user field to obtain our target's name while the "contacts" data source should be used to populate the "contacts" table! And that's it for passing our data. Before moving on it's worth mentioning that the choosing the <code>EntryListTableData</code> class to wrap our contact data was highly dependent on the way we have implemented the contact relationship. For other circumstances another of the provided <code>TableData</code> classes may be more appropriate or you can always implement your own class for the job, check the link above for details.

Now, how to produce and save our pdf file:

		template.saveInstancePDF("path/to/pdf/" + targetPerson.name + " Contacts.pdf");
It looks very simple but a lot is going on behind the scenes here. First the template is instantiated, being read internally into a proper structure and having it's fields and tables populated there, then the resulting template instance is sent to an external process to be converted to a pdf, received back and finally saved to disk at the file path you specified. The Template class offers multiple similarly simple methods to obtain the instance and its pdf and also to save any of them to disk or to a stream, read about it in the [Instantiating, Converting to PDF and Saving](#instantiating-converting-to-pdf-and-saving) section. As mentioned above you'll need an external process whenever you need a pdf though, this is because ODDJET relies on a pre-existing OpenOffice process to make the conversion from odt to pdf. You can read more about it on the [OpenOffice Headless Process and Connection](#openOffice-headless-process-and-connection) subsection. For now, providing you have libreoffice or openoffice installed, you can just blindly run the following command on a terminal:

	$ soffice --headless --accept="socket,host=<host>,port=<port>;urp;"
Take care to do this after closing the template document and any other processes of Libre/OpenOffice or it will not work.

Finally we should clean up our template object before repeating the process with the next person. However, we don't really have to in this case. Since we keep using the same parameter names and the <code>add</code> methods overwrite any previous information there will be no complaints or accumulating of old data if we just skip this step. Still, for educational purposes, here's how we could do this:

	template.removeParameter("person");
    template.removeTableDataSource("contacts");
or

	template.clearParameters();
    template.clearTableDataSources();

With this you should have all the necessary knowledge to finish and run this example. If you have any doubts check out our [main class](src/example/quickstart/java/main/Example.java). 

###### TL;DR?

_Check and run the [example files](src/examples/quickstart/)!_

## Usage Instructions

As demonstrated in the [Quickstart](#quickstart) section, the general method to use ODDJET for templating is simple:

* Write a template document in odt format following the rules established by ODDJET for its variable structures.
* Construct a Template class (or superclass) object.
* Pass this object the template document and its locale. 
* Pass it the data required for the document.
* Call the appropriate Template class methods to fill the template document's variable structures with the data and save the resulting document instance.

From here on each section will focus on explaining aspects and components of ODDJET in detail, containing all the necessary information to follow the steps above and use ODDJET efficiently. We'll begin by exploring the main API classes for ODDJET, the template class and the table data source classes, followed by the more important template document structures to use with ODDJET, also unveiling a bit of the internal workings of ODDJET and how they affect or are affected by these structures, then we'll expose some problems that may arise while using some template documents (and how to solve them) and finally we explain how it is possible to be more aware of what is happening behind the scenes and to detect errors with more ease by activating the log.

### The Template Class

The template class connects one odt template file to the data required to instantiate it into a integral odt document. It contains the mechanisms to populate the template document with the data, the instantiation mechanisms, and provides the methods to print the instance document to pdf or save any of the resulting files to disk.

To allow production of documents of different languages using the same data, the template class also contains a Locale object that is used to obtain localized representations of the data.

The Template object is not dependent on the template document on disk after it has been instantiated or has had <code>setDocument</code> called successfully since it reads and stores the document in memory.

#### Template Data

The template class stores two types of data to be used in populating the document templates. The data parameters, representing single data units, and the table data sources, data collections to be displayed in tables within the document.

Each of those parameters and data sources must have an associated name so that they can be identified in different structures of the template odt document. Due to some of ODDJET's functionalities and due to limitations of the odt format and of the editor software these names must follow some rules. Data parameter names cannot contain dots and data source names must consist of alphanumeric and underscore characters only. The reasons behind these rules will become apparent in following sections where the mechanisms that deal with these data types are explained further.

Each of these types of data are therefore stored in different map collections with String keys while the Template class provides the adequate methods to manipulate them such as <code>get</code>, <code>add</code>, <code>remove</code> and <code>clear</code> methods. It's worthy to mention that modifying the maps returned by the getter methods will not alter the maps within the Template class instance, forcing the use of the appropriate methods for the effect. It's is also important to note that the base data Objects within the getter returned map are not clones however, meaning any changes to their internal state is maintained.

Both parameters and internal data objects in the collections are always referred as Object class instances to allow ODDJET to accept any kind of data. When populating the document their string representation is usually obtained via the <code>toString()</code> method. However, in order to deal with localization, the process begins by searching the object for a <code>getContent(Locale)</code> method. If this method exists, it is evaluated with the Template class object's locale and the toString() method applied to this call's result instead. If the result is <code>null</code> a <code>getContent()</code> method is searched for and called with the same intent.* Only if an <code>Exception</code> occurs while attempting to call the <code>getContent(Locale)</code> method will <code>toString()</code> be called on the original object. If the final content or original object is null an empty string is returned.

* Note that if the <code>getContent(Locale)</code> does not exist, <code>getContent()</code> is not used. This is to avoid replacing the original object when the fetched content is not <code>Locale</code> related.

#### Instantiating, Converting to PDF and Saving

The template class provides several methods to instantiate the template, convert the instances to pdf and save odt or pdf instances.

Of these <code>getInstance()</code> is the most important method since all others rely on it to generate the instance odt document. It uses the data within the template class to fill the variable content structures within the file to instantiate the template. Note that this method works with the current data in the template and it does not fail if there are structures in the template document that cannot be filled by the current data, keeping them as they are. If it's not possible to load the set template document an exception is thrown specifying the problem.

The method to obtain the instance document converted to pdf, <code>getInstancePDFByteArray()</code>, relies on an external OpenOffice process to make the conversion. An exception is thrown if the process is not available. Details on how to run and configure such a process are given in the next section. Note that the intermediate odt instance supplied to the process is not saved to disk so that no temporary files are created and the process is faster.

The save methods allow writing the instance documents to an OutputStream or to a file on disk given either a file path String or a File. When it's not possible to save the file to the specified medium an exception is thrown specifying the problem.

##### OpenOffice Headless Process and Connection

The printing of a template document instance to pdf is done through an OpenOffice headless service, so the template class expects to have one of these services available when any method involving pdf conversion is called. The expected service's host address and port are specified as ODDJET module parameters in the configuration.properties file, oddjet.openoffice.service.host and oddjet.openoffice.service.port respectively. If the service is not available then an OpenOfficeConnectionException is thrown.

To launch one of these services it is only necessary to have libreoffice or openoffice installed in the host and run one simple command:

    $ soffice --headless --accept="socket,host=<host>,port=<port>;urp;" &

Here the <code>--headless</code> flag is what makes this soffice process a pure service with no GUI or default document. The <code>--accept</code> flag specifies the types of connections the service accepts. In this flag's value, _<port>_ is the configured port for the service and _<host>_ is either the configured host address or, more simply, <code>0</code> meaning connections are accepted from all available network interfaces. To be more specific the <code>--accept</code> flag's value is formatted as part of a [UNO URL](http://www.openoffice.org/udk/common/man/spec/uno-url.html), consisting of its the connection and protocol parts. So far, ODDJET only accepts socket connection types and the urp protocol so these are constant in the code above. 

The example bash script file [here](src/main/scripts/OpenOfficeService.sh) runs this command with the default module configuration for the host and port. 

Please note that when headless soffice services are already running normal soffice processes will silently fail to run and vice-versa.

#### Instance Page Count

The template class also provides a method to return a current instance document's page count. This method exists to help in cases where the page count influences the data in some way. In the future other methods that like this one expose the instance's statistics may be implemented. Please note that these type of method need to generate an instance in order to count its pages and so they are very time consuming.

### Table Data Collection Classes

The classes that represent a table data source always implement the interface <code>TableData</code>. The purpose of this interface is to enable the table filling mechanisms in the document generation process to abstract from the original structure of the data by always relying on positionally structured data.

The interface defines two methods that are responsible for transforming the original data into a positional structure and returning it, <code>getData()</code> and <code>getData(List&lt;String&gt;)</code>. The former method transforms the data using a default internal ordering while the latter allows the user to interfere with the positional order of named informational categories within the data. The exact nature of these categories and how the supplied order is interpreted is left to be defined by each implementing class.

The chosen positional representation for the content is a list of lists of objects. The top level list represents the table while the internal lists represent either columns or rows within the table. Each data object within an internal list is to be mapped to a cell within its matching cell range. The coordinates of the cell matching each data object are therefore decided by the object's index within the internal list and the index of the internal list within the top level list. Null values for these lists are taken to mean that the matching table, column, row or cell is static. Despite being positional, the lists are not required to have their size match the table's size nor have the same size between themselves since any missing trailing values are assumed as null.

ODDJET already supplies three common implementing classes:

* <code>PositionalTableData</code> - Constructed from already positional data constructs. By default it returns an exact conversion of the original constructs into our list representation. It also allows to jumble the original data's internal level structures by accepting an order of index categories (integer strings). Any non-index category or index that is out of bounds of the data will cause a null value to be inserted in the top level list.
* <code>CategoricalTableData</code> - Constructed with straightforward string categories in the form of a associative map of strings to object lists. By default it uses the order in which the map iterates over entries, but the user can specify the included categories and their order by providing the corresponding strings in the proper order. If a provided string is not found within the original map a null value will be inserted in the top level list. 
* <code>EntryListTableData</code> - Constructed from an iterable of objects, the table entries. This class allows building complex tables from these entries by supporting the same mechanisms as used for [data parameter name matching](#data-parameter-name-matching) when dealing with categories. By default it returns a top level list containing a single list containing the entry objects. The user can however use attribute chain notation in the categories to include any public data available within the entry object to any depth. On failure to resolve the attribute chain in an entry object, a null value will be placed instead of the data. 

Note that, in these implementing classes, each call to <code>getData</code> returns a new structure that can be changed without affecting the original data. Data objects will not be cloned, however, and therefore all modifications they suffer will be permanent.

### User Fields for Single Data Parameters

User fields allow using a single variable data string in a template document. Through a user field this string may be used within the text or in conditions within conditional content structures.

In the process of instantiating a template the names of the user fields within the template document are matched against the parameters supplied to the template class object. In case a match is found for a field's name, the matching object's string representation for the template's locale is obtained, as specified at the end of the [Template Data](#template-data) section, and set as its value.

#### Data Parameter Name Matching

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

#### Working Examples

A field

    User Field person.father.spouse.nationality.foreign = 
could correspond to the object obtained evaluating

    Template.getParameters().get("person").getFather().getSpouse().getNationality().isForeign()
resulting in setting the field value to "true" or "false".
___

A field

    User Field image_background.color = N/A
could correspond to the object obtained evaluating

    Template.getParameters().get("image_background").color()
resulting in setting the field value to a localized string representation of a color such as "Red", "Rojo", "Rød", "Rouge" or "Vermelho".
Also, in this example, if there was a failure resolving the attribute chain the field value would be kept, providing "N/A" as an acceptable default value.

#### Notes, Caveats and Recommendations

* To obtain a method the first letter in the attribute's name does not need to be in upper case, this is done automatically.
* The attribute chain notation prohibits the names of template parameters to contain dots.
* Failure in resolving an attribute chain is not equal to obtaining null as the final result. In that case, the field's value is set to null's string representation - the empty string.
* Some care may be needed with the order in which an attribute is searched within the object since our conflict resolution is based on it. For example, if a class implements <code>Map\<String,?\></code> and has a key with the same name as one of its attributes then the matching process will always retrieve the key's value and not the attribute's.
* The chain mechanism allows to call any 0-argument method whether they return an object or not. In the latter case the result will just be null. No type of side-effect contention is performed so care is recommended to avoid data inconsistencies due to those. While doing this intentionally may be useful in some cases, it is generally spurned as it violates the separation of responsibilities by having the template instantiation process alter data.

### Tables for Data Collections

Tables in template documents allow displaying the data collections stored in the template class as table data sources. They dictate the parts of the data that are to be used, via the names of informational categories within the data source, as well as some aspects of their population and generation process, via configuration parameters.

Regarding the specification of the participating informational categories, these are chosen by writing their names as the single content of a paragraph on the cells of the first row or column after the header, depending on whether their data is to be expanded vertically or horizontally. These category names are gathered into the list, in left-to-right or top-to-bottom order, that is passed as an argument to the <code>getData(List&lt;String&gt;)</code> method of the table's matching data source. Their interpretation is therefore left to the data source's class. The paragraph containing the categories are removed later to allow placing the proper data on the cell.

As for the configuration parameters, these are passed via the table's corresponding call (similar to a function call) along with the matching data source's name. Table calls are associated with the template document's tables as their name. ODDJET will attempt to populate any table whose name follows the notation for a table call, taking it as a dynamic. The table call's notation and specificities and the existing configuration parameters will be detailed in the next sections.

The tables in the document do not need to have the dimensions to accommodate all of the data a priori. The table grows as data is placed into it. The only dimension requirements are:

* The table must have more rows and columns than those specified as header space, via the [Header parameter](#header), which is fixed and remains unaltered as the table is populated.
* If the data source is to be used not as a whole but via its informational categories ([Content Structure](#content-structure) is **categorical**), then the table must have at least one more row or column, depending on the [Content Direction parameter](#content-direction), beyond the header space, where the category names are supposed to be written.
* The table must contain the cells to be used as initial style sources, meaning that it must have at least the number of columns and rows specified as relative coordinates through the [Style Source parameter](#style-source), beyond the header space.

These columns and rows mentioned above are enough to generate the table since their cells will work as template cells for all the new cells that are created.

Be careful that, in case the data source is used as a whole, any excessive data or categories will be ignored if their placement in the table would force the creation of a new header cell. You can view the header space as establishing a contract prohibiting data to be placed beyond its reach.

Also another important aspect to be taken into account with table dimensions is that any extra space in the table beyond what the data source can occupy will not be removed or altered.

#### Table Calls

Table calls define a dynamic table's data source, the way it is styled and populated with the data and, optionally, an identifier to help differentiate it from other tables with the same data source.

A table call has the following notation - _[table name]_ or _[table name]_**(**_[table parameter list]_**)**. Here, _[table name]_ is either _[data source name]_ or _[data source name]_**_**_[id number]_ where the data source name is 1 or more alphanumeric or underscore characters and the id number are one or more digits.\* The _[table parameter list]_ is a comma separated list of table configuration parameter values. The parameter types and their possible values are detailed in the following section, [Table Configuration Parameters](#table-configuration-parameters), but at the table call level parameter values are only examined generically as consisting of a sequence of one or more alphanumeric or underscore characters.

This notation can accept most document table names as it is only slightly more restrictive. However, the data source name will be searched for within the template's table data sources and in case none is found the table is assumed to be static without consequences apart from a warning log. Even so, in order to be completely safe from any confusion between static and dynamic tables, it is recommended to mark static tables in the document with a symbol, such as # or $, in their name.

The processing mechanism for creating the table's configuration from the parameter list is very tolerant:
* Parameter values are assumed to be case-insensitive
* Parameter values do not have to be in a specific order
* Each parameter has a default value to be used in case they are not present in the list
* Conflicts stemming from different values for the same parameter type within the list are solved by their order, with the last one being the one that is used, since the list is processed sequentially. 
* Unknown parameter values, those that may represent a generic parameter value but are not recognized as belonging to any parameter type, are ignored apart from triggering a warning log. This is particularly useful to enable old versions of the API to produce results while evaluating templates with new features they do not recognize.

\* Beware that the data source name is not greedy, meaning that if the data source name is terminated by an underscore followed by numbers it is necessary for that table to have an id number, otherwise that termination will be taken to be the table's identifier and the table data source will not be found.

#### Table Configuration Parameter Types

##### Header

The header template configuration parameter specifies the table's header section limits via the coordinate of the first non-header cell.
        
###### Parameter Values:

* **nhr** or **noheader** - the table has no headers. The first non-header cell's column and row indexes are 0.
* **hdr**_[col idx]_**_**_[row idx]_ or **header**_[col idx]_**_**_[row idx]_ - the table header has _[col idx]_ columns and _[row idx]_ rows or, interchangeably, the first non-header cell's column index is _[col idx]_ and row index is _[row idx]_.

The parameter's default value is (0,1), meaning the header section of the table is comprised of a single row.
     
##### Content Structure

The content structure table configuration parameter specifies the way the table's data content is to be used to generate the table.

###### Parameter Values: 

* **pos** or **positional** - use content as structured by position, data content will be used as a whole disregarding categorical structure and order.
* **cat** or **categorical** - use content as structured by categories, the categories to be used are specified within the table in the proper order.

The parameter's default value is **categorical**.
 
##### Content Direction

The content direction table configuration parameter specifies the direction in which the table's data is placed. The meaning of this parameter changes a litlle bit according to the content structure parameter. In case of a categorical content structure, this enumeration specifies the direction in which data categories will be expanded while generating the table. In case of a positional content structure, since there is no concept of category expansion, it simply specifies whether the coordinates of the data are to be inverted, flipping the table.

###### Parameter Values:

* **ver** or **vert** or **vertical** - Content is vertical, categorical data is expanded in the columns. Positional data remains unaffected, as this is taken to be its default direction.
* **hor** or **horz** or **horizontal** - Content is horizontal, categorical data is expanded in the rows. Positional data is flipped.

The parameter's default value is **vertical**.

##### Fill Behavior

The fill behavior table configuration parameter specifies what is done with a cell's corresponding data when the cell is already filled with some content. Empty paragraphs are not counted as content here since they may be used only for content positioning purposes. Only if a paragraph contains any character, including white space characters, is it considered as content. This implies that white space can be useful to intentionally leave some cells blank within a table.

###### Parameter Values:

* **skp** or **skip** - skip the cell if it has content, keeping the cell's corresponding data to be applied in the next cell. Write the data otherwise.
* **stp** or **step** - step to the next cell if it has content, discarding the corresponding data. Write the data otherwise.
* **wrt** or **write** - write the corresponding data to the cell independently of it having content or not.

The parameter's default value is **write**.

##### Write Behavior

The write behavior table configuration parameter specifies where in the cell, relative to its current content (including empty paragraphs), is its corresponding data written.

###### Parameter Values:

* **apd** or **append** - append the data to the cell's last paragraph or place it in a new paragraph if there is none.
* **ppd** or **prepend** - prepend the data to the cell's first paragraph or place it in a new paragraph if there is none.
* **ovw** or **overwrite** - inserts a new paragraph containing the data in the cell, removing all other paragraphs within the cell.

The parameter's default value is **append**.


##### Style Source

The style source table configuration parameter specifies the source of each table cell's style, including border style. It allows to either express that there is no need to style the cells, or what are the relative coordinates of the cell from where style should be copied from. This parameter is most useful for tables whose body is generated dynamically since the newly generated body columns/rows are added with just the document's default style.

###### Parameter Values:

* **pre** or **prest** or **prestyled** - the table is already styled as intended
* **vst** or **vertst** or **verticalstyle** - the table is to be styled vertically, meaning styles vary only between columns. The corresponding relative coordinates are (0,1) so the style is copied from the previous cell in the column.
* **hst** or **horzst** or **horizontalstyle** - the table is to be styled horizontally, meaning styles vary only between columns. The corresponding relative coordinates are (1,0) so the style is copied from the previous cell in the row.
* **pst**_[col idx]_**_**_[row idx]_ or **perdst**_[col idx]_**_**_[row idx]_ or **periodicstyle**_[col idx]_**_**_[row idx]_ - the table is periodically styled, meaning styles vary periodically between columns, rows or a combination of both. _[col idx]_ and _[row idx]_ specify the relative coordinate so the style is copied from the cell that is _[col idx]_ columns previous and _[row idx]_ rows previous to the target cell.

The parameter's default value is **verticalstyle**.

##### Last Border

The last border table configuration parameter is used when it is necessary to have the last border in a dynamically generated table be different from the borders found in body columns/rows. The last border will be dependent of the table's Content Direction. If vertical then it will consist of the bottom borders of the cells in the last row. If horizontal then it consist of the right borders of the cells in the last column. 

The simplest example where this parameter is necessary is in a box table where while the body's columns/rows mustn't have a right/bottom border the last column/row must have it to close the box. Since there is no concept of fixed footer like there is for the header, it is not possible to accomplish this without the parameter.*

This parameter is gravely limited, it only specifies either to use an empty border or to choose an existing border from a limited set within the table to copy. The selectable borders within the table are the left, right, bottom or top borders from either the header or the body. Notice that it's possible to select the same border as we're trying to change, this will of course maintain the target border as is. Considering each section is a rectangle, the selectable borders are picked from the top left cell (for left and top borders) or the bottom right cell (for bottom and right borders) of the section. This is not the case however, for the header section when it contains both columns and rows. In that case the bottom or right borders are not selectable, meaning that choosing these values for the last border parameter will produce no effect. Notice that in that case the table dimensions are known a priori and so the parameter is technically unnecessary since then the table can be formatted before the instantiation. Still, its use is accepted even if the style source parameter indicates the body's cells are already pre-styled.

Given its limitations and the guarantee of future changes in the approach to this problem, it is recommended to limit its use to the cases where it is strictly necessary.

\* The fixed header concept may help when it's required that a first border be different from the body borders and there is no headers. This can be achieved by having one column/row of header instead of zero, setting the proper border in it and reducing the its dimension to the border dimension, making this column/row near unnoticeable apart from the desired border. This is not a very proper approach, however, and so improving our approach to this problem is within our future plans.

###### Parameter Values:

* **nlb** or **nolborder** or **nolastborder** - the table's last border is to be replaced by the empty border.
* **lb_**_[border reference]_ or **lborder_**_[border reference]_ or **lastborder_**_[border reference]_ - the table's last border is to be replaced by the referenced border. The border reference notation is _[section][type]_.

In the last case, _[section]_ matches:
* **h** or **header** - The referenced border is in the header section of the table.
* **b** or **body** - The referenced border is in the body section of the table.
    
And _[type]_ matches:
* **l** or **left** - The referenced border is the left border of the section.
* **r** or **right** - The referenced border is the right border of the section.
* **t** or **top** - The referenced border is the top border of the section.
* **b** or **bottom** - The referenced border is the bottom border of the section.  

The parameter's default value is for it to be undefined meaning that the last border is kept as is.

#### Automatic Table-Dependent Template Data Parameters

After a table is created the following data parameters are automatically inserted as user fields into the template document with their appropriate values:

* _[table name]_**_nRow** - The table's total number of rows.
* _[table name]_**_nCol** - The table's total number of columns.
* _[table_name]_**_nData** - The total number of cells where data has been written.

These are meant to be used as user fields or in conditional structures that are dependent of the table's dimensions.

### Conditional or Hidden Fields and Sections for Conditional Content

ODDJET does not have any special mechanisms to deal with conditional content, relying completely on the structures made available in the ODF specification for the effect. To achieve proper separation of responsibilities most if not all the logic concerning the documents contents should be dealt with through them.

The most common structures available are:
* Conditional Text Field - Allows to choose between two text spans based on a condition. Only regular text is allowed and both text spans are formated equaly and uniformely.
* Hidden Text Field - Hides a span of text based on a condition. Only regular text is allowed.
* Hidden Paragraph Field - Hides the paragraph of text where it is located based on a condition. The paragraph has no outstanding limitations.
* Hidden Sections - Hides an entire section of the document based on a condition. The section has no outstanding limitations.

These structures do not always cover all our needs, in which case it is necessary to include some logic while passing data to the Template class object, or force us to create complicated structures to realize simple logic tasks. An example of such a case is the inability to include fields in conditional or hidden text and of different formats in the former, forcing the duplication of the complete paragraph and the use of hidden paragraphs with opposing conditions. Future plans include implementing a solution to simplify these cases and improve the user experience.

### Template Document Problems

The API ODDJET relies on to modify the odt files recently has some issues processing certain tables in documents. Succintly these are: 

* Cell borders types dashed and double are not supported, usually being replaced with default border styles.
* Cell and border styles may disappear or get mixed up in the table.
* Unexpected exceptions may occurr during the process.

The first issue for now remains untreated because it is purely API dependent. It is recommended not to use these types of borders in dynamic tables.

The second and third issues stem from unrecognized or unexistent inner components of the odt file, meaning they can be avoided by sanitizing the template odt files. To fix these issues quickly we provide a bash script [here](src/main/scripts/template/hotfix), hotfix.sh. To use this script simply call it with the path to the template directory and the target template file name as arguments, for example:

	$ hotfix.sh ./templates TemplateExample.odt

It is recommended to backup the original documents before running the script because if any problem occurrs in the process the templates may be left in an inconsistent state.

Note that all scripts present in the directory linked above must be next to the hotfix script since they are used in it. Also note that there may be python scripts requiring python 3 to be installed in the system to run.

### Logging

As can be inferred from the previous sections, ODDJET is very permissive and ignores most errors or suspicious constructs it finds on the template document. This may lead to some confusion when resulting documents appear to have missing or incorrect information. To avoid this ODDJET makes use of the logging abstractions of [SLF4J](http://www.slf4j.org/) to inform the user of any noteworthy events in the instantiation process whenever required. SLF4J is very flexible and, by supporting multiple logging frameworks, gives you the freedom to control the application's logs as you wish.

To quickly and easily enable logging all that is required is to place one of the SLF4J bindings jars (like _slf4j-simple.jar_ for example) in the application's classpath. These bindings can be extracted from the SLF4J distribution that you can [download here](http://www.slf4j.org/download.html). For more information on how to configure SLF4j and the underlying logging frameworks check out their [documentation](http://www.slf4j.org/docs.html). SLF4J's [short manual](http://www.slf4j.org/manual.html) is a great place to start. 

## LibreOffice Writer Tips and How To's

This section gives some tips and explains how to use and create the odt format structures that are most important for our API using LibreOffice Writer. Although the interface's differ, most tips and explanations are valid for other word processors such as OpenOffice Writer too.

### Fields

#### Creating

* Access the Fields window via the menu Insert > Fields > Other... or by hitting Ctrl-F2.
* Pick the type of field by choosing the tab and corresponding type within the tab.
* Choose/fill the field's parameters.
* Place the cursor in the location where you want to insert the field.
* Click Insert or hit Alt-i with the Fields window selected.

Creating user fields is easy using the method above, however since these are used for our API some cares are necessary:
* It is recommended to set the Format parameter as "Text" or "General", other formats may lead to erroneous data representations and condition evaluations.
* Even if a field is only to be used in conditions it may still be useful to include it within the text in a special section of the document and check "invisible" in its parameters. This helps prevent accidental deletions and has no effect on a document print/preview. 
* If a field is to be static for some reason, make sure there is no parameter of the template object with a matching name. Unlike with table calls, there is no restriction to accepted user field names and, therefore, no way of marking static user fields within the document.

##### Notably Useful Field Types

* Variables > User Field - To use single data variables within the text or in conditions.
* Functions > Hidden Text/Conditional Text/Hidden Paragraph - To develop conditional content.
* Document  > Date/Page/Statistics/Time - To avoid passing these types of data through parameters without need.

#### Deleting

Most fields can be deleted just by removing them from the text, however to delete a user field this is not sufficient. To remove the field completely it's necessary to select it in the Fields Window and click the red X button close to the field's value. This button is only available if the field is not used in the text.

Be careful when removing certain user fields since it is possible to remove those that are present only within conditions of sections or other fields. This does not result in an error, just in the field not being filled with the template parameter's data, possibly causing the involved conditions to evaluate erroneously.

#### Updating

Sometimes, after the field's value has changed, the change is not reflected on the text instances of the field immediately. If necessary, field value updates can be forced by selecting in the menu Tools > Update > Fields or by pressing F9.

### Tables
![under construction](http://upload.wikimedia.org/wikipedia/commons/2/20/UnderCon_icon.svg "Under Construction!")
### Conditional Content
![under construction](http://upload.wikimedia.org/wikipedia/commons/2/20/UnderCon_icon.svg "Under Construction!")
#### Conditional Text
#### Hidden Text
#### Hidden Paragraphs
#### Hidden Sections
## Links of Interest

* [LibreOffice Writer Guide 4.0](https://wiki.documentfoundation.org/images/3/35/WG40-WriterGuideLO.pdf)
* [LibreOffice Getting Started Guide](https://wiki.documentfoundation.org/images/1/13/GS40-GettingStartedLO.pdf)
* [LibreOffice Community Support](http://www.libreoffice.org/get-help/community-support/)
	* [Ask.LibreOffice](http://ask.libreoffice.org/en/questions/)
	* [The Document Foundation Wiki](https://wiki.documentfoundation.org/Main_Page)
* [Simple Logging Facade for Java (SLF4J)](http://www.slf4j.org/)

For developers:
* [Apache ODF Toolkit (incubating)](http://incubator.apache.org/odftoolkit/simple/) - 
	* [Simple API javadoc](http://incubator.apache.org/odftoolkit/mvn-site/0.8-incubating/simple-odf/apidocs/index.html)
	* [Simple API Cookbook](http://incubator.apache.org/odftoolkit/simple/document/cookbook/index.html)
	* [ODFDOM OpenDocument API](http://incubator.apache.org/odftoolkit/odfdom/index.html)
* [OASIS Open Document Format for Office Applications (OpenDocument)](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=office)
	* [Open Document Format v1.2](http://docs.oasis-open.org/office/v1.2/OpenDocument-v1.2.pdf)
* [JODConverter](http://www.artofsolving.com/opensource/jodconverter)

## Future Plans
![under construction](http://upload.wikimedia.org/wikipedia/commons/2/20/UnderCon_icon.svg)
<!--
* paragraphs, bullet/numbered enumerations
* extend category format to include all the attribute chain functionality on all TableData cases
* better approach to first/last borders
* template cells
* textual logic constructs.
* Allow indexed access to iterable Object's content's via the field names.
* data ordering
* do more statistics methods like get page count. Maybe do some kind of cache for them so that a call to one already populates the values of others until template contents are changed.
* find solutions to all the problems with the simple api and the odt format.
* use the log more often! (INFO level)
* expose more conversions?
* improve the separation of the formatting and logic from the java and the simplicity of the template by deducing some of the table parameters and maybe
--> 