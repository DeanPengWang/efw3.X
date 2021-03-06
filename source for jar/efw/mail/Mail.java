/**** efw3.X Copyright 2016 efwGrp ****/
package efw.mail;

import java.io.StringWriter;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import efw.efwException;

/**
 * MailをテンプレートXMLのmailタグとマッピングし、1つのMailを表すクラス。
 * @author Chang Kejun
 *
 */
public final class Mail {
	/**
	 * SqlタグからSqlオブジェクトを作成する。
	 * @param element　Sql外部化XMLのsqlタグ
	 * @param lastModifytime　最終更新日時
	 * @throws efwException　タグ不正のエラー。
	 */
	protected Mail(Element element,Date lastModifytime) throws efwException{
		String tmpParamPrefix=element.getAttribute("paramPrefix");
		//もしSQLに:がある場合、paramPrefixを別文字に設定するようにできる
		if(tmpParamPrefix!=null&&tmpParamPrefix.length()>0)paramPrefix=tmpParamPrefix;
		
		this.lastModifytime=lastModifytime;
		NodeList nodes=element.getChildNodes();
		for(int i=0;i<nodes.getLength();i++){
			Node node=nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE){
				Element step= (Element)node;
				if (step.getTagName().equals("to")){
					to= step.getTextContent();
				}else if (step.getTagName().equals("cc")){
					cc= step.getTextContent();
				}else if (step.getTagName().equals("bcc")){
					bcc= step.getTextContent();
				}else if (step.getTagName().equals("subject")){
					subject= step.getTextContent();
				}else if (step.getTagName().equals("body")){
					body= step.getTextContent();
				}else if (step.getTagName().equals("from")){
					from= step.getTextContent();
				}else{
					String information;
					try{
						StreamResult xmlOutput = new StreamResult(new StringWriter());
						Transformer transformer = TransformerFactory.newInstance().newTransformer();
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
						transformer.transform(new DOMSource(node), xmlOutput);
						information = xmlOutput.getWriter().toString();
					}catch(Exception e){
						information=e.getMessage();
					}
					throw new efwException(efwException.XMLTagIsNotLegalException,information);
				}
			}
		}
	}
	private String to;
	protected String getTo(Map<String,String> params){
		return replaceParams(to,params);
	}
	private String cc;
	protected String getCc(Map<String,String> params){
		return replaceParams(cc,params);
	}
	private String bcc;
	protected String getBcc(Map<String,String> params){
		return replaceParams(bcc,params);
	}
	private String subject;
	protected String getSubject(Map<String,String> params){
		return replaceParams(subject,params);
	}
	private String body;
	protected String getBody(Map<String,String> params){
		return replaceParams(body,params);
	}
	private String from;
	protected String getFrom(Map<String,String> params){
		return replaceParams(from,params);
	}
	private String replaceParams(String data,Map<String,String> params){
		if (data==null)return null;
		String temp=data;
		for(Entry<String, String> e : params.entrySet()) {
			temp=temp.replaceAll(paramPrefix+e.getKey(), e.getValue());
		}
		return temp;
	}
	/**
	 * Mailにパラメータを識別するための頭文字
	 */
	private String paramPrefix=":";
	protected String getParamPrefix(){
		return paramPrefix;
	}
	/**
	 * 最終更新日時。
	 */
	private Date lastModifytime;
	protected Date getLastModifytime() {
		return lastModifytime;
	}

}
