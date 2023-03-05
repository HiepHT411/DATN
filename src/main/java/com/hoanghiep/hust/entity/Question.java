package com.hoanghiep.hust.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "questions")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int quesId;
	private String title;
	private String optionA;
	private String optionB;
	private String optionC;
	private String optionD;
	private int ans;
	private int chose;

	@ManyToOne
	@JsonIgnore
	private Part part;

	@Column(name = "idx")
	private int index;

	@OneToOne
	@JsonIgnore
	private QuestionStackDirections questionStackDirections;

	@Column(name = "image", columnDefinition = "TEXT")
	private String image;

	public Question(int quesId, String title, String optionA, String optionB, String optionC, String optionD, int ans, int chose, int index) {
		super();
		this.quesId = quesId;
		this.title = title;
		this.optionA = optionA;
		this.optionB = optionB;
		this.optionC = optionC;
		this.optionD = optionD;
		this.ans = ans;
		this.chose = chose;
		this.index = index;
	}

	@Override
	public String toString() {
		return "Question [quesId=" + quesId + ", index= " + index + ", title=" + title + ", optionA=" + optionA + ", optionB=" + optionB + ", optionC=" + optionC + ", ans=" + ans + ", chose=" + chose + "]";
	}

}
