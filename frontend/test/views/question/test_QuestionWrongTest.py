import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/questionWrong"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, target_url: str):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    page.goto(target_url)
    return browser, context, page

@pytest.mark.parametrize("credentials, role", [
    (TEACHER_CREDENTIALS, "teacher"),
    (STUDENT_CREDENTIALS, "student"),
    (ADMIN_CREDENTIALS, "admin")
])
def test_question_wrong_render(playwright, credentials, role):
    browser, context, page = login_and_get_page(playwright, credentials, BASE_URL)
    if role == "admin":
        # 管理员应被重定向到/manage/teachers
        expect(page).to_have_url(re.compile(r"http://localhost:3000/manage/teachers"))
        context.close()
        browser.close()
        return
    if role == "teacher":
        # 教师应被重定向到首页
        expect(page).to_have_url(re.compile(r"http://localhost:3000/$"))
        context.close()
        browser.close()
        return
    expect(page).to_have_url(re.compile(r"http://localhost:3000/questionWrong"))
    expect(page.locator("h2")).to_have_text("错题本")
    # 检查表格渲染或空提示
    if page.locator(".resource-table").count() > 0 and page.locator(".resource-table").is_visible():
        expect(page.locator(".resource-table")).to_be_visible()
        # 检查题目详情弹窗
        preview_btns = page.locator("button.preview")
        if preview_btns.count() > 0:
            preview_btns.first.click()
            expect(page.locator(".modal-header h3")).to_have_text("题目详情")
            expect(page.locator(".modal-body")).to_be_visible()
            page.locator(".modal-close").click()
            expect(page.locator(".modal-mask")).not_to_be_visible(timeout=3000)
        # 检查删除错题按钮
        delete_btns = page.locator("button.delete")
        if delete_btns.count() > 0:
            delete_btns.first.click()
            # 删除后页面未报错
            expect(page.locator("body")).to_be_visible()
    else:
        assert page.locator(".empty-state").is_visible()
    context.close()
    browser.close()
