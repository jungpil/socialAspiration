# **conf** folder

- Configuration files (experimental setup) should be stored here.  
- Run by invoking ">> java app.Simuation conf/testing.conf"


# File Format
```
experiment=<String; within or between>
numFirms=<int array; number of firms, comma-separated>
iterations=<int; number of simulated time ticks to run>
uncertainty=<double; uncertainty parameter; Greve (2002) used 0.5 and 2.0 for low and high uncertainty, respective>
numTraits=<int; number of traits that define a firm>
numTraitValues=<int; number of values each firm trait can take>
replacementCutoff=<double; multiplier for replacement>
traitsToChange=<int array; number of traits to change when changing strategy; comma-separated>
socialDistance=<double array; threshold of neighborhood inclusion for reference group; comma-separated>
outfile=<String; name of output file; if omitted, STDOUT will be used>
```

# Parameter explanation
```
experiment : "within" has all firms be the same (i.e., no replacement); "between" has firms with different socialDistance parameters
numFirms : number of firms
iterations : number of simulated time ticks to run
uncertainty :uncertainty parameter that determines actual performance variation from strategy; Performance = Strategy + (uncertainty x epsilon)
numTraits : number of traits that define a firm; complexity of industry
numTraitValues : number of values each firm trait can take; maturity of industry
replacementCutoff : multiplier for replacement (perf < avgPerf - c*stdPerf); default = 1.28, weak = 1.96, strong = 0.84); irrelevant if experiment=within
traitsToChange : number of traits to change when changing strategy; ease of strategic change
socialDistance : to include as peer reference group other firms with distance <= socialDistance (high social distance is more strict -- if socialDistance=1 then only firms with exactly same trait values will be considered neighbors)
```

# Default Values
```
experiment=within
numFirms=10,
iterations=250
uncertainty=0.5
numTraits=10
numTraitValues=2
replacementCutoff=1.28
traitsToChange=1
outfile=testing
```

# Sample Config Setups 
## Within Type 
```
experiment=within
numFirms=200
iterations=250
uncertainty=0.5
numTraits=10
numTraitValues=2
replacementCutoff=1.28
traitsToChange=1
socialDistance=0.9
outfile=within_200_low_4_2_default_1_0.8
```

## Between Types
```
experiment=between
numFirms=200,200,200,200,200
iterations=250
uncertainty=0.5
numTraits=10
numTraitValues=2
replacementCutoff=1.28
traitsToChange=1,1,1,1,1
socialDistance=0.9,0.8,0.7,0.6,0.5
outfile=between_200_low_10_2_default_1_0.9-0.5
```
