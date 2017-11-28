valTemp = 16;
valChauff = 12;

function basse(x) {
	if (x <= 10) return 1;
	else if (x >= 20) return 0;
	else return (-1.0/10.0) * x + 2.0;
}

function moyenne(x) {
	if (x <= 10 || x >= 30) return 0;
	else if (x <= 20) return (1.0/10.0) * x - 1.0;
	else return (-1.0/10.0) * x + 3.0;
}

function haute(x) {
	if (x <= 20) return 0;
	else if (x >= 30) return 1;
	else return (1.0/10.0) * x - 2.0;
}

function minBM(x) {
	return minOf(basse(x), moyenne(x));
}

function minMH(x) {
	return minOf(haute(x), moyenne(x));
}

function minBH(x) {
	return minOf(haute(x), basse(x));
}

function maxBM(x) {
	return maxOf(basse(x), moyenne(x));
}

function maxMH(x) {
	return maxOf(haute(x), moyenne(x));
}

function maxBH(x) {
	return maxOf(haute(x), basse(x));
}

function maxBMH(x) {
	return maxOf(maxOf(moyenne(x), basse(x)), haute(x));
}

function chauff(x) {
	if(x<=8) return 0;
	else if (x >= 10) return 1;
	else return (1.0/2.0) * x - 4.0;
}

function chauffTrunc(x, truncValue) {
	if(x<truncValue) return chauff(x);
	else return chauff(truncValue);
}

function reverseChauff(x) {
	return 2.0 * x + 8;
}

print(valTemp + " degree : ");
print("basse : " + basse(valTemp));
print("moyenne : " + moyenne(valTemp));
print("haute : " + haute(valTemp));

b = newArray(9);
m = newArray(9);
h = newArray(9);
mibm = newArray(9);
mimh = newArray(9);
mibh = newArray(9);
mabm = newArray(9);
mamh = newArray(9);
mabh = newArray(9);
mabmh = newArray(9);
x = newArray(0, 5, 10, 15, 20, 25, 30, 35, 40);
 
for(i = 0; i <= 40; i += 5) {
	b[i / 5] = basse(i);
	m[i / 5] = moyenne(i);
	h[i / 5] = haute(i);
	mibm[i / 5] = minBM(i);
	mimh[i / 5] = minMH(i);
	mibh[i / 5] = minBH(i);
	mabm[i / 5] = maxBM(i);
	mamh[i / 5] = maxMH(i);
	mabh[i / 5] = maxBH(i);
	mabmh[i / 5] = maxBMH(i);	
}

/*
Plot.create("Fuzzy partition", "Temp", "Y");
Plot.setLimits(0, 40, 0, 1);
Plot.setColor("blue");
Plot.add("line",x , b);
Plot.setColor("orange");
Plot.add("line",x , m);
Plot.setColor("red");
Plot.add("line",x , h);
Plot.show();
*/

Plot.create("Basse ou Moyenne", "Temp", "Y");
Plot.setLimits(0, 40, 0, 1);
Plot.setColor("blue");
Plot.add("line",x , mabm);
Plot.show();

Plot.create("Min", "Temp", "Y");
Plot.setLimits(0, 40, 0, 1);
Plot.setColor("blue");
Plot.add("line",x , mibm);
Plot.setColor("orange");
Plot.add("line",x , mimh);
Plot.setColor("red");
Plot.add("line",x , mibh);
Plot.show();

Plot.create("Max Basse Moyenne", "Temp", "Y");
Plot.setLimits(0, 40, 0, 1);
Plot.setColor("blue");
Plot.add("line",x , mabm);
Plot.show();
Plot.create("Max Moyenne Haute", "Temp", "Y");
Plot.setLimits(0, 40, 0, 1);
Plot.setColor("orange");
Plot.add("line",x , mamh);
Plot.show();
Plot.create("Max Basse Haute", "Temp", "Y");
Plot.setLimits(0, 40, 0, 1);
Plot.setColor("red");
Plot.add("line",x , mabh);
Plot.show();
Plot.create("Max Basse Moyenne Haute", "Temp", "Y");
Plot.setLimits(0, 40, 0, 1);
Plot.setColor("green");
Plot.add("line",x , mabmh);
Plot.show();

truncValue = reverseChauff(basse(valChauff));
print("Trunc Value for " + valChauff + ": " + truncValue);

x = newArray(0, 5, 8, truncValue, 10, 15);
chauffage = newArray(6);
chauffImp = newArray(6);

for(i = 0; i < 6; i++) {
	chauffage[i] = chauff(x[i]);
	chauffImp[i] = chauffTrunc(x[i], truncValue);	
}

Plot.create("Chauff", "Temp", "Y");
Plot.setLimits(0, 15, 0, 1);
Plot.setColor("blue");
Plot.add("line",x , chauffage);
Plot.setColor("red");
Plot.add("line",x , chauffImp);
Plot.show();


Plot.create("Chauff", "Temp", "Y");
Plot.setLimits(0, 15, 0, 1);
Plot.setColor("blue");
Plot.add("line",x , chauffage);
Plot.show();

a = 15 - truncValue;
c = truncValue - 8;
b = 15 - 8;
h = chauff(truncValue);

result = ((2.0 * a * c + a * a + c * b + a * b + b * b) / (3 * (a + b))) + 8;
print("centroide Res: " + result);