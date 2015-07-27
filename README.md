# DroidPrint

## Contents

- [JavaDocs](##javadocs)
- [Installation](#installation)
- [Basic Usage](#basic-usage)
    + [ImageAsset](#imageasset)
    + [PrintItem](#printitem)
    + [printJobData](#printJobData)
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

    project(':droidprint').projectDir = new File('../DroidPrint/app')

It should look something like this:

    include ':app', ':droidprint'
    project(':droidprint').projectDir = new File('../DroidPrint/app')

You must also include the project in your gradle file:

    compile project(':droidprint')

## Basic Usage

In order to use the __DroidPrint__ library, you need to first obtain the URI to the bitmap you want to print.
Once you have that, you are ready to invoke our print workflow.

### ImageAsset

One of the first objects you need to create in order to use our print workflow is the `ImageAsset` object. It tells the print sdk what you want to print.
You will need at least one but you can create as many as needed ([Multiple Assets](#multiple-assets)). You can use one of the following 3 methods to create the imageAsset.

```java
ImageAsset imageAsset4x6 = new ImageAsset(this, R.drawable.template4x6, ImageAsset.MeasurementUnits.INCHES, 4, 6);
```

```java
ImageAsset assetdirectory = new ImageAsset(this, "oceanwave.jpeg", ImageAsset.MeasurementUnits.INCHES, 4, 6);
```

```java
ImageAsset bitmapAsset = new ImageAsset(this, bitmap, ImageAsset.MeasurementUnits.INCHES, 4,5);
```

Or, if you already saved the bitmap into the internal storage location for your app:

```java
ImageAsset imageAsset4x6 = new ImageAsset(fileUriString, ImageAsset.MeasurementUnits.INCHES, 4, 6);
```

### PrintItem

Once you create an image asset, you need to associate it with a `PrintItem` object. This object provides a scaletype (layout) for the imageAsset and allows you to define what media size you want to associate with the `ImageAsset`. You will need to provide a `ScaleType` and the `ImageAsset` itself.

```java
PrintItem printItemDefault = new ImagePrintItem(PrintItem.ScaleType.CENTER, imageAsset4x6);
```

For more information, take a look in the Sample App or the JavaDocs.

### PrintJobData

After creating the `PrintItem`, you are ready to create the `PrintJobData` object which contains all the relevant info needed
to build the print.

```java
PrintJobData printJobData = new PrintJobData(activity, printItemDefault);
printJobData.setJobName("Example");
PrintUtil.setPrintJobData(printJobData);
```

### Print

Once you have created the `PrintJobData` object, you are ready to print.

```java
PrintUtil.print(activity);
```

## Advanced

If you would like more customization on what gets printed when users select certain print settings in the Android Print Dialog,
we optionally provide a way to give the `PrintJobData` multiple `PrintItems` that will be selected based on your user's media size selection as
well as orientation.

### Multiple Assets

When creating an `PrintItem`, you can provide the constructor a MediaSize object that lets the Print Library know that you
would like the PrintItem to be used for a particular paper size selection. For example, if you did:

```java
PrintItem printItemLetter = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER, scaleType, imageAsset4x5);
```

It would tell the Print Library that you want to use the `imageAsset4x5` whenever the user selects the letter size media in
the Android Print Dialog.

You must then add the printItem to the `PrintJobData` by invoking:

```java
printJobData.addPrintItem(printItemLetter);
```

### Default Print Attributes

`printJobData` takes in an Android standard [`PrintAttributes`](https://developer.android.com/reference/android/print/PrintAttributes.html) object which is used to set print dialog options.

```java
PrintAttributes printAttributes = new PrintAttributes.Builder()
        .setMediaSize(printItemLetter.getMediaSize())
        .build();
printJobData.setPrintDialogOptions(printAttributes);
```

### Print Metrics

Printing related metrics will be sent to HP server.  HP is collecting information like: wireless setup, printer id, number of pages, black and white filter, and print job status. See `PrintMetricsData` for details.

Above printing information is also available to your app. In order to allow us to post printing metrics data to your app, please implement `PrintUtil.PrintMetricsListener` in your calling activity.

```java
class YourCallingActivity extends ActionBarActivity implements PrintUtil.PrintMetricsListener {
	...
	@Override
    	public void onPrintMetricsDataPosted(PrintMetricsData printMetricsData) {
   			// Do what you want to do with available printMetricsData
    	}
	...
}
```

### Plugin Install Helper

You can disable the our print plugin install helper by setting:

```java
PrintUtil.showPluginHelper = false;
```
