package actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constants.AttributeConst;
import constants.ForwardConst;
import constants.PropertyConst;

//各アクションクラスの親クラス
public abstract class ActionBase {
    protected ServletContext context;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    //初期化処理
    public void init(
            ServletContext servletContext,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse){
        this.context  = servletContext;
        this.request  = servletRequest;
        this.response = servletResponse;
    }

    //フロントコントローラから呼び出されるメソッド
    public abstract void process() throws ServletException,IOException;

    //パラメータのcommandの値に該当するメソッドを実行
    protected void invoke()throws ServletException,IOException{
        Method commandMethod;
        try {
            //パラメータからコマンドを取得
            String command = request.getParameter(ForwardConst.CMD.getValue());

            //commandに該当するメソッドを実行
            commandMethod = this.getClass().getDeclaredMethod(command, new Class[0]);
            commandMethod.invoke(this, new Object[0]); //引数なし
        }catch(NoSuchMethodException | SecurityException | IllegalAccessException |
                IllegalArgumentException| InvocationTargetException | NullPointerException e) {
            //commandの値が不正で実行できない場合
            forward(ForwardConst.FW_ERR_UNKNOWN);
            e.printStackTrace();
        }
    }

        //指定されたjspの呼び出しを行う
        protected void forward(ForwardConst target)throws ServletException,IOException{
            //jspファイルの相対パスを作成
            String forward = String.format("/WEB-INF/views/%s.jsp",target.getValue());
            RequestDispatcher dispatcher  = request.getRequestDispatcher(forward);

            //jspファイルの呼び出し
            dispatcher.forward(request,response);
        }

        //URLを構築しリダイレクトする
        protected void redirect(ForwardConst action,ForwardConst command)throws ServletException,IOException{
            String redirectURL = request.getContextPath()+"/?action="+action.getValue();
            if(command !=null) {
                redirectURL = redirectURL +"&command="+command.getValue();
            }
            response.sendRedirect(redirectURL);
        }

        protected boolean checkToken()throws ServletException,IOException{
            //トークン取得
            String _token = getRequestParam(AttributeConst.TOKEN);
            if(_token == null||!(_token.equals(getTokenId()))) {

                forward(ForwardConst.FW_ERR_UNKNOWN);
                return false;
            }else {
                return true;
            }

        }
//セッションID取得
        protected String getTokenId() {
            return request.getSession().getId();
        }
//要求ページ数を取得
        protected int getPage() {
            int page;
            page = toNumber(request.getParameter(AttributeConst.PAGE.getValue()));
            if(page==Integer.MIN_VALUE) {
                page = 1;
            }
            return page;
        }

        //文字列を数字に変換
        protected int toNumber(String strNumber) {
            int number = 0;
            try {
                number = Integer.parseInt(strNumber);
            }catch(Exception e) {
                number = Integer.MIN_VALUE;
            }
            return number;
        }

        //文字列をLocalDate型に変換
        protected LocalDate toLocalDate(String strDate) {
            if(strDate == null||strDate.equals("")) {
                return LocalDate.now();
            }
            return LocalDate.parse(strDate);
        }
//TODO 以下あいまいな理解
        //リクエストスコープから指定されたパラメータの値を取得し、返却する
        protected String getRequestParam(AttributeConst key) {
            return request.getParameter(key.getValue());
        }

        //リクエストスコープにパラメータを設定
        protected<V> void putRequestScope(AttributeConst key,V value) {
            request.setAttribute(key.getValue(), value);
        }

        //セッションスコープから指定されたパラメータの値を取得し、返却
        @SuppressWarnings("unchecked")
        protected<R> R getSessionScope(AttributeConst key) {
        return (R)request.getSession().getAttribute(key.getValue());
        }

        //セッションスコープにパラメータを設定
        protected<V> void putSessionScope(AttributeConst key,V value) {
            request.getSession().setAttribute(key.getValue(),value);
        }
        //セッションスコープから指定された名前のパラメータを除去する
        protected void removeSessionScope(AttributeConst key) {
        request.getSession().removeAttribute(key.getValue());
        }

        //アプリケーションスコープから指定されたパラメータの値を取得し、返却する
        @SuppressWarnings("unchecked")
        protected<R> R getContextScope(PropertyConst key) {
            return (R)context.getAttribute(key.getValue());
        }


}
