(* ::Package:: *)

y:=ToExpression[Import["C:\\Users\\W7\\Desktop\\Projekty\\TankWars\\newton\\newton.txt"]]
f[x_]:=Simplify[y]
(*str:=ExportString[f[x],"C"]*)
Export["C:\\Users\\W7\\Desktop\\Projekty\\TankWars\\newton\\fx.txt",CForm[f[x]]]
Export["C:\\Users\\W7\\Desktop\\Projekty\\TankWars\\newton\\newton.png", 
Plot[f[x],{x,-20,180}]]



