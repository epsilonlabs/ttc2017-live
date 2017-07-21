package org.eclipse.epsilon.examples.ttclive2017;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.etl.EtlModule;

public class App {
	
	public static void main(String[] args) throws Exception {
		new App().run();
	}
	
	protected void run() throws Exception {
		
		String modelEvnironmentVariable = System.getenv("Model");
		String transformationEnvironmentVariable = System.getenv("Transformation");
		String runIndexEnvironmentVariable = System.getenv("RunIndex");
		
		long startTime = System.nanoTime();
		long startMemory = Runtime.getRuntime().freeMemory();
		
		EPackage refinementsEcore = registerEcore("RefinementsEcore.ecore");
		EPackage simpleCodeDomEcore = registerEcore("SimpleCodeDOM.ecore");
		
		EtlModule module = new EtlModule();
		module.parse(App.class.getResource("RefinementsEcore2SimpleCodeDOM" + transformationEnvironmentVariable + ".etl").toURI());
		
		EmfModel in = new EmfModel();
		in.setName("In");
		in.setModelFile(System.getenv("Input"));
		in.setMetamodelUri(refinementsEcore.getNsURI());
		in.setReadOnLoad(true);
		in.setStoredOnDisposal(false);
		in.load();
		
		EmfModel out = new EmfModel();
		out.setName("Out");
		out.setModelFile(System.getenv("Output"));
		out.setMetamodelUri(simpleCodeDomEcore.getNsURI());
		out.setReadOnLoad(false);
		out.setStoredOnDisposal(true);
		out.load();
		
		module.getContext().getModelRepository().addModel(in);
		module.getContext().getModelRepository().addModel(out);
		
		module.execute();
		module.getContext().getModelRepository().dispose();
		module.getContext().dispose();
		
		System.out.println("Epsilon;" + modelEvnironmentVariable + ";"+ transformationEnvironmentVariable + ";" 
				+ runIndexEnvironmentVariable + ";Time;" + (System.nanoTime() - startTime));
		System.out.println("Epsilon;" + modelEvnironmentVariable + ";"+ transformationEnvironmentVariable + ";" 
				+ runIndexEnvironmentVariable + ";Memory;" + (Runtime.getRuntime().freeMemory() - startMemory));
		
	}
	
	protected EPackage registerEcore(String ecore) throws Exception {
		XMIResource resource = new XMIResourceImpl();
		resource.load(App.class.getResourceAsStream(ecore), null);
		EPackage ePackage = (EPackage) resource.getContents().get(0);
		EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
		return ePackage;
	}
	
}
