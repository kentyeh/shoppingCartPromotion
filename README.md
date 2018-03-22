# Shopping Cart Promotion Caculator #

[![Build Status](https://travis-ci.org/kentyeh/shoppingCartPromotion.svg?branch=master)](https://travis-ci.org/kentyeh/shoppingCartPromotion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.kentyeh/scpc/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.kentyeh/scpc)
[![Javadocs](http://www.javadoc.io/badge/com.github.kentyeh/scpc.svg?color=blue)](http://www.javadoc.io/doc/com.github.kentyeh/scpc)
[![Contributor](http://wsbadge.herokuapp.com/badge/Developer-Kent%20Yeh-oragnle.svg)](https://github.com/kentyeh)

This project elaborates how a shopping cart can promote flexible.

I have heard about some promotion, e.g.

 * Buy N get free 1 offer,
 * Bundle sale get N% off,
 * Buy two, get last one 50% off,
 * Gift with purchase,
 * ...,  and  so on
 
 There is too many options make it harder to design. A universal solution is to formulate the promotion.
   
## Cart Items ##

In my concern, cart items, include not merely products but also rewards and even discounts generated from promotions.
the process should be:

 * Per single item spread from cart items for iteration.
 * Promotion rule filters those qualified items.(I say the Rule contains Item)
  * Rules loops items and record statistics to make a decision.
    * Rules detect whether a single item for the purpose to generate a bonus.
    * Generate a bonus or process next rule when a single item matched.
## Components ##

### IItem ###
Represent any one put in shopping cart, includes products , bonuses or discounts.

### SingleItem ###
The first procedure is to spread items into SingleItems in the cart.

Item                        |Original Price | Sale Price  |      | Quantity
------------------------ | ---------:| ---------:|:---:| ----:
Stapler                     | $15.99 | $10.59 | x |1
Liquid Paper                | $8.25  | $6.98  | x |2
Precision Compass           | $12.64 | $12.64 | x |1
BrightPink Fabric Marker Pen| $8.8   | $7.93  | x |2
Purple Fabric Marker Pen    | $8.23  | $7.4   | x |2

Shopping cart rearranges into single items( ordered by its price).

Single Item                | serialNum | serialLast
------------------------| ---: | ---
Precision Compass           | 1 | true
Stapler                     | 1 | true
BrightPink Fabric Marker Pen| 1 | false
BrightPink Fabric Marker Pen| 2 | true
Purple Fabric Marker Pen    | 1 | false
Purple Fabric Marker Pen    | 2 | true
Liquid Paper                | 1 | false
Liquid Paper                | 2 | true

### IRule ###
Represent how promotion process.

IRule walks through all SingleItems, filters them, find triggered, collection statistics and calculate how many bonuses should rewards.

#### Filters Items ####
Suppose we have a promotion rule for pens. Then we have to find out those contains items and collection statistics (the capital name in the parentheses represents the variable) when iterating.

Single Item                 | Contains Count<br/>(N) | Original Price<br/>(OP)| Sale Price<br/>(SP) |serialNum<br/>(NN) | Sum Of OP<br/>(SCOP) | Sum Of SP<br/>(SCSP)| Sum of Serial OP<br/>(SSOP) | Sum of Serial SP<br/>(SSSP)
------------------------ | ---: | ---: | ---: | ---:  | ---:  | ---: | ---: | ---:
BrightPink Fabric Marker Pen | 1 | $8.8  | $7.93 | 1 | $8.8   | $7.93  | $8.8   | $7.93
BrightPink Fabric Marker Pen | 2 | $8.8  | $7.93 | 2 | $17.6  | $15.86 | $17.6  | $15.86
Purple Fabric Marker Pen     | 3 | $8.23 | $7.4  | 1 | $25.83 | $23.26 | $8.23  | $7.4
Purple Fabric Marker Pen     | 4 | $8.23 | $7.4  | 2 | $34.06 | $30.66 | $16.46 | $14.8

#### Item triggered to decide bonus ####
Let's assume the above promotion is **buy two get free one offer**, It is evident that something triggered at **N%2==0**, and result in a bonus.

Single Item                 | Contains Count<br/>(N) | Triggered | Decision 
------------------------ | ---: | --- | --- 
BrightPink Fabric Marker Pen | 1 | False |  
BrightPink Fabric Marker Pen | 2 | True  | Get a free one brightpink pen. 
Purple Fabric Marker Pen     | 3 | False |  |
Purple Fabric Marker Pen     | 4 | True  | Get a free one purple pen. 

### BonusItem ###
An IItem as a reward when shopping item matches condition.

### CurrentItem ###
Alternative BonusItem, an IItem represents the current visit as a reward when shopping item matches condition.

### EvalHelper ###
An object to help evaluate a formula.

Java build in a script engine from version 1.6. It is a convince way use to evaluate a formula.

I defined an EvalHelper class for testing.

```java
public class JsEvalFactory extends JsEvalHelper<CartItem> {
  @Override
  public String getVarSalePrice() {
    return "SP";
  }

  @Override
  public String getVarContainsCount() {
    return "N";
  }
  ...
}
```

### AbstractRuleBase ###
A helper class help implements IRule.

AbstractRuleBase uses two formulas: **triggerFormula** for determining 
current visit SingleItem wether should trigger, and **quantityFormula** for determining the quantity of reward 
should give if SingleItem was successfully triggered.

A EvalHelper is required to evulate formulas.

### Calculator ###
```java
 static Collection<BonusItem> calcBonus(List<IRule> rules, Set<IItem> cartItems) throws ScriptException;
```
A static method to calculate rewards from **rules** and ***cartItems***.

<br/>

## Usage ##
Perhaps you have already your own CartItem class, If it did, a wrapper class implements IItem needs.

I did a CartItem class to represent shopping cart item and a JsEvalFactory class to define some variables.

There are two types of rule:
 * ILeafRule: a terminal rule for generating rewards.
 * IChainRule: used for bundle sale promotion.

It is a convince way to extends AbstractRuleBase to build your promotion rule.

## Rule Cases ##
The above case **"Buy two get free one offer"**, It could be

triggerFormula | Bonus |quantityFormula |
----- |  --- | --- |
N%2==0 | CurrentItem | 1 | 

or alternative **"Buy three get one is free"**

triggerFormula | Bonus |quantityFormula |
----- |  --- | --- |
N%3==0 | One Cent Discount | SPx10 | 

The latter is slightly worse than the former, because someone may forget to put 3x items into his shopping cart.

One thing to notice, do not apply one cart item into multiple rules except make sure that the higher 
priority rule earns more benefit than the lower. e.g., two rules **Buy two get free one offer** vs 
**Buy three to save 10 %**, I don't know which one is a better deal, sometimes wrong rule order 
makes the wrong decision.

### Quantity Discount ####
There is a promotion : **Buy 5+ get 2%,buy 10+ get 3% and buy more then 20+ get 5% discount** .

triggerFormula | Bonus |quantityFormula 
----- |  --- | --- 
N>4 | One Cent Discount | SCSPx(N>19?5:N>9?3:N>4?2:0)x10


Single Item                 | Contains Count<br/>(N) | Sum Of SP<br/>(SCSP)| Decision 
------------------------ | ---: | ---: | ---
BrightPink Fabric Marker Pen | 1 | $7.93  | 
BrightPink Fabric Marker Pen | 2 | $15.86 |
... | ... |... |
BrightPink Fabric Marker Pen | 5 | $39.65  |  Get $39.65 x 0.02 x 10= 7.93 cents discount.
... | ... |... |
BrightPink Fabric Marker Pen | 10 | $79.3  |  Get $79.3 x 0.03 x 10 = 23.79 cents discount.
Purple Fabric Marker Pen     | 11 | $86.7 | Get $86.7 x 0.03 x 10 = 26.01 cents discount.
... | ... |... |
Purple Fabric Marker Pen     | 20 | $153.3 | Get $153.3 x 0.05 x 10 = 76.65 cents discount.
Purple Fabric Marker Pen     | 21 | $160.7 | Get $160.7 x 0.05 x 10 = 80.35 cents discount.

Evidently, only last decision can accept. so we have to set

```java
ILeafRule.isLastQuantityOnly() == true
```
Then we get 80 cents discount.

<div style="color:red;font-weight:bold">Notice:</div>

It always needs to set ```isLastQuantityOnly==true``` when using "SUM OF XXX PRICE" variables.

### Original Price Discount ###
It seems to me that this promotion is not so instinctive, but in Taiwan, many retail like to play this trick.

Promotion rule : **Buy 2x pens get Original Price 30% discount** 

```java
isLastQuantityOnly==true
```

triggerFormula | Bonus |quantityFormula 
----- | --- | --- 
N%2==0 | One Cent Discount | (SCSP-SCOPx0.7)x10 

### Buy Same Items Got Discount ###
How about **"Buy two same pens get free one offer"**?

triggerFormula | Bonus |quantityFormula 
----- | --- | --- 
NN%2==0 | CurrentItem | 1

Another case is **"Buy 2 same pens get Original Price 30% discount"** 

```java
ILeafRule.isLastQuantityOnly() == flase
```
triggerFormula | Bonus |quantityFormula 
----- | --- | --- 
NN%2==0 | One Cent Discount | (SP-OPx0.7)x10x2

## Bundle Sale Promotion ##
It is difficult to complete Bundle-Sale in one rule, we can approach this by using ```IChainRule```.

There is a bundle-sale of stationery **"One stapler bundle with 2 liquid paper and 3 pens get 10% discount"**,

First IChainRule: deal with stapler.

triggerFormula | contains | next rule
----- | --- | ---
true | stapler | deal with liquid paper

Second IChainRule: deal with liquid paper.

triggerFormula | contains | next rule
----- | --- | ---
N%2==0 | liquid paper | deal with pens

Last ILeafRule: deal with pens.

triggerFormula | contains | Bonus |quantityFormula 
----- | --- | --- | --- 
N%3==0 | All Pens | One Cent Discount | (SCSP+PSCSP+PPSCSP)x10

There are two variables never see here.

I had already defined a JsEvalFactory class as follow: 

```java
public class JsEvalFactory extends JsEvalHelper<CartItem> {
  @Override
  public String getPreviousRulePrefix() {
    return "P";
  }
}
```

PSCSP is same as SCSP, except it is for its parent rule.

PPSCSP also same as PSCSP, it represents the SCSP of stapler rule.

<div style="color:red">Finally, once the ILeafRule got reward successfully, all three relative rules' accumulating price will be reset to zero.</div>

## Maven ##
```xml
<dependency>
    <groupId>com.github.kentyeh</groupId>
    <artifactId>scpc</artifactId>
    <version>1.0</version>
</dependency>
```