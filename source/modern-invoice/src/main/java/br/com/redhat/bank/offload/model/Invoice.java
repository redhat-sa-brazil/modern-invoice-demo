package br.com.redhat.bank.offload.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;

@Entity
@Table(name = "invoice")
@ProtoDoc("@Indexed")
public class Invoice implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@ProtoDoc("@Field")
	@ProtoField(number = 1)
	Integer id;

	@ProtoDoc("@Field")
	@ProtoField(number = 2)
	String customerName;

	@ProtoDoc("@Field")
	@ProtoField(number = 3)
	String dueDate;

	@ProtoDoc("@Field")
	@ProtoField(number = 4)
	Double total;

	public Invoice(){}

	public Invoice(Integer id, String customerName, String dueDate, Double total) {
		this.id = id;
		this.customerName = customerName;
		this.dueDate = dueDate;
		this.total = total;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Invoice other = (Invoice) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Invoice [customerName=" + customerName + ", dueDate=" + dueDate + ", id=" + id + ", total=" + total
				+ "]";
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}	
   
}