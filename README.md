# DroidPrint

## Contents

- [JavaDocs](##javadocs)
- [Installation](#installation)
- [Basic Usage](#basic-usage)
    + [ImageAsset](#imageasset)
    + [PrintItem](#printitem)
    + [PrintJob](#printjob)
- [Advanced](#advanced)
    + [Multiple Assets](#multiple-assets)
    + [Default Print Attributes](#default-print-attributes)
    + [Print Metrics](#print-metrics)
    + [Plugin Install Helper](#plugin-install-helper)

## Features

1. Kitkat print preview
2. Dialogs for assisting users in installing print plugins
3. More advanced layouting features than google's `PrintHelper`

## JavaDocs

The JavaDocs for this project are located in the Documentation directory of the source code.

## Installation

The __DroidPrint__ library is not yet available publicly via maven or jcenter.
Currently, to install the plugin, you need to download the source code and compile it into your application.

Clone the source code into the same parent directory as your android app.

Insert the following into your settings.gradle file in your project at the end of the the 'include' line:

    ':droidprint'

Add the following new line into the same settings.gradle file in your project:

    project(':droidprint').projectDir = new File('../DroidPrint')

It should look something like this:

    include ':app', ':droidprint'
    project(':droidprint').projectDir = new File('../DroidPrint')

You must also include the project in your gradle file:

    compile project(':droidprint')

## Basic Usage

In order to use the __DroidPrint__ library, you need to first obtain the URI to the bitmap you want to print.
Once you have that, you are ready to invoke our print workflow.

### ImageAsset

One of the first objects you need to create in order to use our print workflow is the `ImageAsset` object.
You will need at least one but you can create as many as needed ([Multiple Assets](#multiple-assets)). For example:

```java
ImageAsset imageAsset4x6 = new ImageAsset(filename4x6, ImageAsset.MeasurementUnits.INCHES, 4, 6);
```

### PrintItem

Once you create an image asset, you need to associate it with a `PrintItem` object. You will need to provide a `ScaleType` and the `ImageAsset` itself.

```java
PrintItem printItemDefault = new ImagePrintItem(PrintItem.ScaleType.CENTER, imageAsset4x6);
```

### PrintJob

After creating the `PrintItem`, you are ready to create the `PrintJob` object which contains all the relevant info needed
to build the print.

```java
PrintJob printJob = new PrintJob(activity, printItemDefault);
printJob.setJobName("Example");
PrintUtil.setPrintJob(printJob);
```

### Print

Once you have created the `PrintJob` object, you are ready to print.

```java
PrintUtil.print(activity);
```

## Advanced

If you would like more customization on what gets printed when users select certain print settings in the Android Print Dialog,
we optionally provide a way to give the `PrintJob` multiple `PrintItems` that will be selected based on your user's media size selection as
well as orientation.

### Multiple Assets

When creating an `PrintItem`, you can provide the constructor a MediaSize object that lets the Print Library know that you
would like the PrintItem to be used for a particular paper size selection. For example, if you did:

```java
PrintItem printItemLetter = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER, scaleType, imageAsset4x5);
```

It would tell the Print Library that you want to use the `imageAsset4x5` whenever the user selects the letter size media in
the Android Print Dialog.

You must then add the printItem to the `PrintJob` by invoking:

```java
printJob.addPrintItem(printItemLetter);
```

### Default Print Attributes

`PrintJob` takes in an Android standard [`PrintAttributes`](https://developer.android.com/reference/android/print/PrintAttributes.html) object which is used to set print dialog options.

```java
PrintAttributes printAttributes = new PrintAttributes.Builder()
        .setMediaSize(printItemLetter.getMediaSize())
        .build();
printJob.setPrintDialogOptions(printAttributes);
```

### Print Metrics

TBD

### Plugin Install Helper

You can disable the our print plugin install helper by setting doing:

```java
PrintUtil.showPluginHelper = false;
```
