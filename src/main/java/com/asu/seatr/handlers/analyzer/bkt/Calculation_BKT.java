package com.asu.seatr.handlers.analyzer.bkt;

import java.util.LinkedList;
import java.util.List;

public class Calculation_BKT {
	private static double DISCOUNT=0.9;  //discount to delay to recommend the tasks with multiple KCs
	
	static private double cal_kc_product(List<Double> kc_p_list){
		double kc_p_product=1.0;
		for (double kc_p:kc_p_list)
			kc_p_product*=kc_p;
		return kc_p_product;
	}
	
	static private double cal_task_correct_p(List<Double> kc_p_list, double slip, double guess){
		double kc_p_product=cal_kc_product(kc_p_list);
		return kc_p_product*(1-slip)+(1-kc_p_product)*guess;
	}
	
	static private List<Double> kc_p_evidence(List<Double> kc_p_list, double slip, double guess, boolean correct){
		double kc_p_product=cal_kc_product(kc_p_list);
		double task_correct_p=cal_task_correct_p(kc_p_list,slip,guess);
		double task_incorrect_p=1-task_correct_p;
		for (int i=0;i<kc_p_list.size();i++){
			double kc_p=kc_p_list.get(i);
			if (correct)
				kc_p=(kc_p_product*(1-slip)+kc_p*(1-kc_p_product/kc_p)*guess)/(task_correct_p);
			else
				kc_p=(kc_p_product*slip+kc_p*(1-kc_p_product/kc_p)*(1-guess))/(task_incorrect_p);
			kc_p_list.set(i, kc_p);
		}
		return kc_p_list;
	}
	
	static private List<Double> extend_kc_p_list_learn(
			List<Double> kc_p_list_learn, //original list
			double kc_p, //probability of mastery of the new KC before learn
			double kc_learn) // probability of master of the new KC after learn
	{
		int l=kc_p_list_learn.size();
		for(int i=0;i<l;i++){
			double kc_p_old=kc_p_list_learn.get(i);
			kc_p_list_learn.set(i, kc_p_old*kc_p);
			kc_p_list_learn.add(kc_p_old*(1-kc_p)*kc_learn);
		}
		return kc_p_list_learn;
	}
	
	static private double cal_sum(List<Double> p_list){
		double sum=0;
		for(double p:p_list){
			sum+=p;
		}
		return sum;
	}
	
	static private double cal_kc_p_learn_all_except(List<Double>kc_p_list, List<Double>kc_l_list, int except_i){
		List<Double>kc_p_list_learn=new LinkedList<Double>();
		kc_p_list_learn.add(1.0);
		for (int i=0;i<kc_p_list.size();i++){
			if (i==except_i)
				continue;
			double kc_p=kc_p_list.get(i);
			double kc_learn=kc_l_list.get(i);
			kc_p_list_learn=extend_kc_p_list_learn(kc_p_list_learn,kc_p,kc_learn);
		}
		return cal_sum(kc_p_list_learn);
	}
	
	static private List<Double> kc_p_learn(List<Double> kc_p_list, List<Double> kc_l_list){
		for (int i=0;i<kc_p_list.size();i++){
			double kc_p=kc_p_list.get(i);
			double kc_l=kc_l_list.get(i);
			double kc_p_new=kc_p+(1-kc_p)*kc_l*cal_kc_p_learn_all_except(kc_p_list, kc_l_list,i);
			kc_p_list.set(i, kc_p_new);
		}
		return kc_p_list;
	}
	
	static public List<Double> update_kc_p(List<Double> kc_p_list, List<Double> kc_l_list, double slip, double guess, boolean correct){
		kc_p_list=kc_p_evidence(kc_p_list, slip, guess, correct);
		kc_p_list=kc_p_learn(kc_p_list, kc_l_list);
		return kc_p_list;
	}
	
	static private List<Double> p_list_comb(List<Double> p_list1, List<Double> p_list2, List<Double> p_list3){
		List<Double> result=new LinkedList<Double>();
		for (int i=0;i<p_list1.size();i++)
			result.add((p_list1.get(i)-p_list2.get(i))*p_list3.get(i));
		return result;
	}
	
	static public double task_utility_kc(List<Double> kc_p_list, List<Double> kc_l_list, List<Double> kc_u_list, double slip, double guess){
		List<Double> kc_p_list_correct=update_kc_p(new LinkedList<Double>(kc_p_list),kc_l_list,slip, guess,true);
		List<Double> kc_p_list_incorrect=update_kc_p(new LinkedList<Double>(kc_p_list),kc_l_list,slip, guess,false);
		double task_correct_p=cal_task_correct_p(kc_p_list, slip, guess);
		double task_incorrect_p=1-task_correct_p;
		double utility=task_correct_p*cal_sum(p_list_comb(kc_p_list_correct,kc_p_list,kc_u_list))*Math.pow(DISCOUNT, kc_p_list.size())
				+task_incorrect_p*cal_sum(p_list_comb(kc_p_list_incorrect,kc_p_list,kc_u_list))*Math.pow(DISCOUNT, kc_p_list.size());
		return utility;
		
	}
	
	static TaskFeature getTaskFeature(String type){
		//assuming N=4; @TODO work on getting the value of N from the front end system
		double N = 4;
		if(type.equals("multiple-choice"))
			return new TaskFeature(0.2 * (1-Math.exp(2-N)), 1/N);
		else if(type.equals("simple-input"))
			return new TaskFeature(0.3,0.01);
		else if(type.equals("normal-input"))
			return new TaskFeature(0.4,0.001);
		else if(type.equals("complex-input"))
			return new TaskFeature(0.5,0.0001);
		else if(type.equals("self-report"))
			return new TaskFeature(0.01,0.7);
		else
			return new TaskFeature(0.2,0.2);
	}
}
