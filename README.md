# Alpine Modeller

*A modular economic-simulation framework*
**Author:** Alper Emre Eren

---

## 1  Overview

Alpine Modeller is a small, self-contained Java 17 desktop application for running
year-by-year macro-economic simulations, executing Groovy scripts on top of the
results, and visualising everything in a Swing GUI.

The code base is deliberately **clean and modular**:

| Layer      | Package                                              | Highlights                                                                |
| ---------- | ---------------------------------------------------- | ------------------------------------------------------------------------- |
| Models     | `net.alperemre.models`                               | `MergedModel` (all economic variables in one place) + simple growth logic |
| Controller | `net.alperemre.controllers`                          | Loading data, binding model fields to Groovy, running scripts             |
| GUI        | `net.alperemre.gui` & `net.alperemre.gui.components` | Swing UI split into reusable panels                                       |
| Utility    | `net.alperemre.util`                                 | Automatic model class discovery                                           |

---

## 2  Quick Start

```bash
git clone <your-repo-url>
cd alpine-modeller
mvn clean package       # builds target/alpine-modeller-1.0.jar
java -jar target/alpine-modeller-1.0.jar
```

> The GUI starts immediately; no command-line arguments are required.

---

## 3  Features

* **Merged economic model** – variables for consumption, investment, exports,
  imports and GDP, each driven by configurable growth indexes.
* **Groovy scripting** – run `.groovy` files or ad-hoc snippets; newly created
  arrays automatically appear as **new columns** in the results table.
* **Dynamic GUI**

    * left: select any model class & data file
    * centre: live results table (years + variables)
    * bottom: buttons to run external or ad-hoc scripts
* **Reflection-based binding** – every public / protected field (even `LL`)
  is exposed to Groovy with **zero boilerplate**.
* **Pluggable** – drop additional `BaseModel` subclasses on the class-path and
  they show up in the GUI automatically.

---

## 4  Prerequisites

| Tool   | Version                                     |
| ------ | ------------------------------------------- |
| JDK    | **17** (tested with Amazon Corretto 17.0.x) |
| Maven  | **3.8+**                                    |
| Groovy | pulled automatically (3.0.x) via Maven      |

---

## 5  Project Layout

```
src/
 ├─ main/
 │   ├─ java/
 │   │   ├─ net.alperemre.annotations/Bind.java
 │   │   ├─ net.alperemre.models/
 │   │   │    ├─ BaseModel.java
 │   │   │    └─ MergedModel.java
 │   │   ├─ net.alperemre.controllers/Controller.java
 │   │   ├─ net.alperemre.gui/
 │   │   │    ├─ DynamicFinalGUI.java
 │   │   │    └─ components/
 │   │   │         ├─ ModelDataPanel.java
 │   │   │         ├─ ResultTablePanel.java
 │   │   │         └─ ScriptButtonsPanel.java
 │   │   └─ net.alperemre.util/ModelDiscovery.java
 │   └─ resources/
 │        └─ (icons / future resources)
 └─ data/
      ├─ data1.txt
      └─ data2.txt
```

---

## 6  Data Format

A data file is plain text; each non-empty line is:

```
<VAR_NAME><whitespace><value_0> <value_1> ... <value_N>
```

* **LATA** row **must be first** and lists the year labels
  (determines `LL`).
* If a row provides fewer than `LL` numbers, the last value is repeated.

Example (`data1.txt`):

```
LATA   2015 2016 2017 2018 2019
twKI   1.03
twKS   1.04
twINW  1.12
twEKS  1.13
twIMP  1.14
KI     1023752.2
KS     315397
INW    348358
EKS    811108.6
IMP    784342.4
```

---

## 7  Running Scripts

### 7.1  External file

1. Press **Run script from file**
2. Choose any `.groovy` file inside `/scripts`

### 7.2  Ad-hoc

1. Press **Create and run ad hoc script**
2. Type Groovy code – example:

```groovy
// GDP growth index vs base year
DPKB = new double[LL]
for (t = 0; t < LL; t++) {
    DPKB[t] = PKB[t] / PKB[0] * 100
}
```

3. Click **Ok** – the table instantly gets a new column *DPKB*.

---

## 8  Extending the Model

1. **Add a new variable**

```java
@Bind public double[] NEW_VAR;
```

2. Reference it in data files and (optionally) in `run()`.

3. Re-run; the GUI will load, show and allow scripting over `NEW_VAR`
   without additional code.

---

## 9  Build & Run Options

| Goal            | Command                         |
| --------------- | ------------------------------- |
| Clean & package | `mvn clean package`             |
| Unit tests      | *(none yet)*                    |
| Uber-jar        | Already created under `target/` |
| Run in IDE      | Launch `DynamicFinalGUI.main()` |

---

## 10  License

This educational project is released under the **MIT License** – feel free to
use, modify and redistribute with attribution.

---

Enjoy modelling!
 ~ **Alper Emre Eren**
