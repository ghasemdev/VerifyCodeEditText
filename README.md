# VerifyCodeEditText

[![Release](https://img.shields.io/github/release/jakode2020/VerifyCodeEditText.svg?style=flat)](https://jitpack.io/#jakode2020/VerifyCodeEditText)
[![Kotlin Version](https://img.shields.io/badge/kotlin-1.4.30-ff8a0d.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-17%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=17)

The use of verify code edit text is to get a one-time code from the user,
There is no default edit text in Android to get a one-time use code.<br>
That's why I decided to build a library so that both xml and kotlin could create such input :)

![alt text][1]

### Gradle Setup

***
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.jakode2020:VerifyCodeEditText:1.2.0'
}
```

## Usage

### XML

```
 <com.jakode.verifycodeedittext.VerifyCodeEditText
     android:layout_width="wrap_content"
     android:layout_height="wrap_content"
     android:layoutDirection="ltr" (RTL language need this field)
     android:paddingBottom="12dp"
     app:BottomErrorIcon="@drawable/bottom_error_icon"
     app:BottomIconHeight="2dp"
     app:BottomIconWidth="40dp"
     app:BottomSelectedIcon="@drawable/bottom_selected_icon"
     app:BottomUnSelectedIcon="@drawable/bottom_unselected_icon"
     app:ItemSpaceSize="28dp"
     app:TextColor="@color/black"
     app:TextFont="@font/baloo"
     app:TextSize="16sp"
     app:ViewCount="Four"/>
```
complete listener
```
 verifyCodeEditText.setCompleteListener { complete ->
     // some code
 }
```
change bottom drawable state
```
 // all bottom drawble show erro state
 verifyCodeEditText.setCodeItemErrorLineDrawable()
 
 // reset drawble to normal state
 verifyCodeEditText.resetCodeItemLineDrawable()
```

![alt text][2]

set / get text
```
 verifyCodeEditText.text = "99999" // set
 println(verifyCodeEditText.text) // get
```

### Kotlin Builder
```
 val verifyCodeEditText = VerifyCodeEditText.Builder {
    text {
        size = 20F
        color = Color.parseColor("#000000")
    }
    bottomIcon {
        iconHeight = 5
        iconWidth = 60
        selectedIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.bottom_selected_icon)
        unSelectedIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.bottom_unselected_icon)
        errorIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.bottom_error_icon)
    }
    verifyCell {
        count = VerifyCodeEditText.Builder.ViewCount.Five
        spaceSize = 48
    }
 }.build(context = this)
 findViewById<ConstraintLayout>(R.id.layout).addView(verifyCodeEditText)
```

### License
***
```
Copyright 2021 Jakode2020 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[1]: ./art/Screenshot.png
[2]: ./art/ScreenshotError.png
