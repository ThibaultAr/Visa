val = 15;

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

print(val + " degree : ");
print("basse : " + basse(val));
print("moyenne : " + moyenne(val));
print("haute : " + haute(val));

b = new Array(9);
m = new Array(9);
h = new Array(9);
x = [0, 5, 10, 15, 20, 25, 30, 35, 40];
for(i = 0; i <= 40; i += 5) {
	b[i / 5] = basse(i);
	m[i / 5] = moyenne(i);
	h[i / 5] = haute(i);
}

plot = new Plot("Fuzzy partition", "Temp", "Y");
plot.setLimits(0, 40, 0, 1);
plot.setColor(Color.blue);
plot.addPoints("line",x , b, Plot.LINE);
plot.setColor(Color.orange);
plot.addPoints("line",x , m, Plot.LINE);
plot.setColor(Color.red);
plot.addPoints("line",x , h, Plot.LINE);
plot.show();
