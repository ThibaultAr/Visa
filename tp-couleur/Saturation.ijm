image = getImageID();
valeur = getNumber ("coefficient multiplicateur", valeur);

Dialog.create("Debut");
Dialog.addMessage("Cliquer sur Ok pour commencer le traitement");
Dialog.show();

setBatchMode(true);

titre=getTitle();
print (titre);
run("Color Space Converter", "from=RGB to=HSB white=D65");
run("Split Channels");
command = titre+" (HSB) (green)";
selectWindow(command);
run("Multiply...", "value=" + valeur);
command = "c1=["+titre+" (HSB) (red)] c2=["+titre+" (HSB) (green)] c3=["+titre+" (HSB) (blue)] ignore"
run("Merge Channels...", command);
run("Color Space Converter", "from=HSB to=RGB white=D65");

setBatchMode(false);

Dialog.create("Fin");
Dialog.addMessage("Cliquez sur OK pour terminer le traitement");
Dialog.show();